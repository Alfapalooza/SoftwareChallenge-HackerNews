package challenge.services.hackerNews

import challenge.guice.Modules
import challenge.logger.impl.ServiceLogger
import challenge.models.hackerNews.User.Id
import challenge.models.hackerNews.{ StoriesWithUsersAndComments, StoryWithUsersAndComments, User }
import challenge.models.responses.hackerNews.{ CommentResponse, StoriesResponse, StoryResponse }
import challenge.services.BaseHttpService
import challenge.utils.AkkaStreamsUtils._
import challenge.utils.OptionUtils._

import com.typesafe.config.Config

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.{ Http, HttpExt }
import akka.http.scaladsl.server.directives.HttpRequestWithEntity
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink }

import scala.collection.immutable
import scala.concurrent.{ ExecutionContext, Future }

class HackerNewsService(modules: Modules) extends BaseHttpService {
  private val hackerNewsServiceConfiguration: Config =
    modules.configuration.underlyingConfig.getConfig("services.hacker-news")

  override val host: String =
    hackerNewsServiceConfiguration.getString("host")

  override val nameForLoggingString: String =
    "HackerNewsService"

  override protected val serviceLogger: ServiceLogger =
    modules.serviceLogger

  override implicit protected val system: ActorSystem =
    modules.akka.actorSystem

  override implicit protected val materializer: ActorMaterializer =
    modules.akka.actorMaterializer

  override implicit protected val executionContext: ExecutionContext =
    system.dispatchers.lookup("services.hacker-news.dispatcher")

  override protected val http: HttpExt =
    Http(system)

  private val parallelismMax: Int =
    hackerNewsServiceConfiguration.getInt("parellelism-max")

  private val numStories: Int =
    hackerNewsServiceConfiguration.getInt("num-stories")

  private val numComments: Int =
    hackerNewsServiceConfiguration.getInt("num-comments")

  private val nestedCommentsDepth: Int =
    hackerNewsServiceConfiguration.getInt("nested-comments-depth")

  private val userTotalCommentCountFlow =
    Flow[User].map(user => user.id -> user.storyCommentsCount)

  private val userStoryCommentCountFlow =
    Flow[User].map(user => user.id -> 1)

  private def flatten[T]: Flow[Seq[T], T, NotUsed] =
    Flow[Seq[T]].mapConcat[T](_.to[immutable.Iterable])

  private def flattenOpt[T]: Flow[Option[T], T, NotUsed] =
    Flow[Option[T]].mapConcat[T](_.to[immutable.Iterable])

  private def userSync(subStreams: Int, userToIdFlow: Flow[User, (Id, Int), NotUsed]) =
    Flow[User]
      .groupBy(subStreams, _.id)
      .via(userToIdFlow)
      .reduce {
        (left, right) =>
          (right._1, left._2 + right._2)
      }
      .mergeSubstreams

  def getTopStories(optNumStories: Option[Int] = numStories.?)(implicit request: HttpRequestWithEntity[_]): Future[StoriesResponse] =
    get("/v0/topstories.json", StoriesResponse.applyHttpResponse(_, optNumStories))

  def getStory(id: Long)(implicit request: HttpRequestWithEntity[_]): Future[Option[StoryResponse]] =
    get(s"/v0/item/$id.json", StoryResponse.applyHttpResponse)

  def getComment(id: Long)(implicit request: HttpRequestWithEntity[_]): Future[Option[CommentResponse]] =
    get(s"/v0/item/$id.json", CommentResponse.applyHttpResponse)

  def getStoryComments(story: StoryResponse, optMaxDepth: Option[Int] = nestedCommentsDepth.?, optNumComments: Option[Int] = numComments.?)(implicit request: HttpRequestWithEntity[_]): Future[StoryWithUsersAndComments] = {
    def getCommentsWithSubComments(id: Long): Future[Seq[CommentResponse]] =
      _getCommentsWithSubComments(Seq(id))

    def _getCommentsWithSubComments(ids: Seq[Long]): Future[Seq[CommentResponse]] = {
      def innerGetComments(ids: Seq[Long], maxDepth: Int, depth: Int = 0): Future[Seq[CommentResponse]] = {
        ids
          .toSource
          .mapAsyncUnordered(parallelismMax)(getComment)
          .via(flattenOpt)
          .runWith(Sink.seq)
          .flatMap { comments =>
            if (maxDepth != -1 && depth > maxDepth) {
              Future.successful(comments)
            } else {
              innerGetComments(comments.flatMap(_.subComments), maxDepth, depth + 1).map { innerComments =>
                comments ++ innerComments
              }
            }
          }
      }

      innerGetComments(ids, optMaxDepth.getOrElse(-1))
    }

    story
      .commentIds
      .toSource
      .mapAsyncUnordered(parallelismMax)(getCommentsWithSubComments)
      .via(flatten)
      .runWith(Sink.seq)
      .map(StoryWithUsersAndComments(story, _))
      .flatMap { story =>
        val users =
          story.users

        users
          .toSource
          .via(userSync(users.size, userStoryCommentCountFlow))
          .runWith(Sink.seq)
          .map { usersCommentCount =>
            story
              .withUpdatedUserStoryCommentCount(usersCommentCount.toMap, optNumComments)
          }
      }
  }

  def getTopStoriesWithUsersAndComments(optNumStories: Option[Int] = numStories.?)(implicit request: HttpRequestWithEntity[_]): Future[StoriesWithUsersAndComments] =
    getTopStories(optNumStories).flatMap(stories => getStoriesWithUsersAndComments(stories.storyIds))

  //O(A + B + C + D)
  def getStoriesWithUsersAndComments(ids: Seq[Long])(implicit request: HttpRequestWithEntity[_]): Future[StoriesWithUsersAndComments] = {
    ids
      .toSource
      .mapAsyncUnordered(parallelismMax)(getStory)
      .via(flattenOpt)
      .async
      .mapAsyncUnordered(parallelismMax)(getStoryComments(_))
      .async
      .runWith(Sink.seq)
      .map(StoriesWithUsersAndComments.apply)
      .flatMap { stories =>
        val users =
          stories.users

        users
          .toSource
          .via(userSync(users.size, userTotalCommentCountFlow))
          .runWith(Sink.seq)
          .map { usersCommentCount =>
            stories
              .withUpdatedUserTotalCommentCount(usersCommentCount.toMap)
          }
      }
  }
}

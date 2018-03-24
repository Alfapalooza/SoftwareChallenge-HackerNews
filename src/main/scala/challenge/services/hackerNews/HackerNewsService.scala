package challenge.services.hackerNews

import challenge.guice.Modules
import challenge.logger.impl.ServiceLogger
import challenge.models.hackerNews.{StoriesWithUsersAndComments, User}
import challenge.models.hackerNews.User.Id
import challenge.models.responses.hackerNews.{CommentResponse, StoriesResponse, StoryResponse}
import challenge.services.BaseHttpService
import challenge.utils.AkkaStreamsUtils._
import challenge.utils.OptionUtils._

import com.typesafe.config.Config

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.server.directives.HttpRequestWithEntity
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

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

  private val prallelismMax: Int =
    hackerNewsServiceConfiguration.getInt("parellelism-max")

  private val numStories: Int =
    hackerNewsServiceConfiguration.getInt("num-stories")

  def getTopStories(optNumStories: Option[Int] = numStories.?)(implicit request: HttpRequestWithEntity[_]): Future[StoriesResponse] =
    get("/v0/topstories.json", StoriesResponse.apply(_, optNumStories))

  def getStory(id: Long)(implicit request: HttpRequestWithEntity[_]): Future[StoryResponse] =
    get(s"/v0/item/$id", StoryResponse.apply)

//  def getComment(id: Long)(implicit request: HttpRequestWithEntity[_]): Future[CommentResponse] =
//    get(s"/v0/item/$id", CommentResponse.apply)
//
//  def getStoriesWithUsersAndComments(ids: Seq[Int]): Future[StoriesWithUsersAndComments] = {
//    val users: Sink[(Id, Int), NotUsed] =
//      Sink.fold()
//
//    val sourceParallelism: Int =
//      if (numStories < prallelismMax)
//        numStories
//      else
//        prallelismMax
//
//    def getStoryComments(story: StoryResponse)(implicit request: HttpRequestWithEntity[_]): Future[Seq[CommentResponse]] =
//      story
//        .commentIds
//        .toSource
//        .mapAsyncUnordered(sourceParallelism)(getComment)
//
//    getTopStories().map { stories =>
//      val storiesWithUsersAndComments: StoriesWithUsersAndComments =
//        stories
//          .storyIds
//          .toSource
//          .mapAsyncUnordered(sourceParallelism)(getStory)
//          .async
//          .mapAsyncUnordered(sourceParallelism)(getStoryComments)
//
//      users
//        .toSource
//          .mapAsyncUnordered(2)()
//    }
//  }
}

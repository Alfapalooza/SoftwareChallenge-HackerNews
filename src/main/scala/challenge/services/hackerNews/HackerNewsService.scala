package challenge.services.hackerNews

import challenge.guice.Modules
import challenge.logger.impl.ServiceLogger
import challenge.models.responses.hackerNews.{CommentResponse, StoriesResponse, StoriesWithUsersAndCommentsResponse, StoryResponse}
import challenge.services.BaseHttpService
import challenge.utils.OptionUtils._

import com.typesafe.config.Config

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.server.directives.HttpRequestWithEntity
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContext, Future}

class HackerNewsService(modules: Modules) extends BaseHttpService {
  private val hackerNewsServiceConfiguration: Config =
    modules.configuration.underlyingConfig.getConfig("services.hacker-news")

  override val host: String =
    hackerNewsServiceConfiguration.getString("host")

  override protected val serviceLog: ServiceLogger =
    modules.serviceLogger

  override implicit protected val system: ActorSystem =
    modules.akka.actorSystem

  override implicit protected val materializer: ActorMaterializer =
    modules.akka.actorMaterializer

  override implicit protected val executionContext: ExecutionContext =
    system.dispatchers.lookup("services.hacker-news.dispatcher")

  override protected val http: HttpExt =
    Http(system)

  private val numStories: Int =
    hackerNewsServiceConfiguration.getInt("num-stories")

  def getTopStories(implicit request: HttpRequestWithEntity[_]): Future[StoriesResponse] =
    get("/v0/topstories.json", StoriesResponse.apply(_, optNumStories = numStories.?))

  def getStory(id: Int)(implicit request: HttpRequestWithEntity[_]): Future[StoryResponse] =
    get(s"/v0/item/$id", StoryResponse.apply)

  def getComment(id: Int)(implicit request: HttpRequestWithEntity[_]): Future[CommentResponse] =
    get(s"/v0/item/$id", CommentResponse.apply)

  def getStoriesWithUsersAndComments(ids: Seq[Int]): Future[StoriesWithUsersAndCommentsResponse] =
    ???
}

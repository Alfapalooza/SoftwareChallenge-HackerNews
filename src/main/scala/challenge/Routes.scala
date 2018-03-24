package challenge

import challenge.guice.Modules
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import play.api.libs.json.Json

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.{ HttpRequestWithNoEntity, MarshallingEntityWithRequestDirective, RequestResponseHandlingDirective }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.util.Timeout

import scala.concurrent.ExecutionContext

trait Routes extends PlayJsonSupport with RequestResponseHandlingDirective with MarshallingEntityWithRequestDirective {
  def modules: Modules

  implicit lazy val timeout: Timeout =
    modules.configuration.timeout

  implicit def system: ActorSystem =
    modules.akka.actorSystem

  implicit def executionContext: ExecutionContext =
    system.dispatcher

  lazy val routes: Route =
    requestResponseHandler {
      pathPrefix("api") {
        pathPrefix("hacker-news") {
          path("top-stories") {
            get {
              extractRequestContext { context =>
                implicit val request: HttpRequestWithNoEntity =
                  new HttpRequestWithNoEntity(context.request)

                complete(modules.hackerNewsService.getTopStories().map { stories =>
                  OK -> Json.toJson(stories)
                })
              }
            }
          }
        }
      }
    }
}

package challenge.models.responses.hackerNews

import challenge.utils.HttpResponseUtils._
import challenge.utils.ThreadPools.sameThread

import play.api.libs.json._

import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer

import scala.concurrent.Future

case class StoriesResponse(storyIds: Seq[Long])

object StoriesResponse {
  implicit val writes: Writes[StoriesResponse] =
    (o: StoriesResponse) =>
      Json.obj(
        "storyIds" -> o.storyIds
      )

  def applyHttpResponse(httpResponse: HttpResponse, optNumStories: Option[Int] = None)(implicit materializer: Materializer): Future[StoriesResponse] =
    httpResponse
      .toJson
      .map(_.as[Seq[Long]])
      .map { ids =>
        optNumStories.fold(ids)(ids.take)
      }
      .map(StoriesResponse.apply)
}
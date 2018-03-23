package challenge.models.responses.hackerNews

import challenge.utils.HttpResponseUtils._
import challenge.utils.ThreadPools.sameThread

import play.api.libs.json._

import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer

import scala.concurrent.Future

case class StoriesResponse(ids: Seq[Int])

object StoriesResponse {
  implicit def writes: Writes[StoriesResponse] =
    (o: StoriesResponse) => Json.obj("ids" -> o.ids)

  def apply(httpResponse: HttpResponse, optNumStories: Option[Int] = None)(implicit materializer: Materializer): Future[StoriesResponse] =
    httpResponse
      .toJson
      .map(_.as[Seq[Int]])
      .map { ids =>
        optNumStories.fold(ids)(ids.take)
      }
      .map(StoriesResponse.apply)
}
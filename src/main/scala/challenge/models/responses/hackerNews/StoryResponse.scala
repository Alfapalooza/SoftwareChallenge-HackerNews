package challenge.models.responses.hackerNews

import challenge.utils.HttpResponseUtils._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer

import scala.concurrent.Future

case class StoryResponse(title: String, commentIds: Seq[Long])

object StoryResponse {
  implicit def reads: Reads[StoryResponse] =
    ((JsPath \ "title").read[String] and (JsPath \ "kids").read[Seq[Long]])(StoryResponse _)

  implicit def writes: Writes[StoriesResponse] =
    (o: StoriesResponse) => Json.obj("storyIds" -> o.storyIds)

  def apply(httpResponse: HttpResponse)(implicit materializer: Materializer): Future[StoryResponse] =
    httpResponse
      .toJson
      .map(_.as[StoryResponse])
}
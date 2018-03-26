package challenge.models.responses.hackerNews

import challenge.utils.HttpResponseUtils._
import challenge.utils.ThreadPools.sameThread

import play.api.libs.json._
import play.api.libs.functional.syntax._

import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer

import scala.concurrent.Future

case class StoryResponse(title: String, commentIds: Seq[Long])

object StoryResponse {
  implicit val reads: Reads[StoryResponse] =
    ((JsPath \ "title").read[String] and (JsPath \ "kids").read[Seq[Long]])(StoryResponse.apply _)

  implicit val writes: Writes[StoryResponse] =
    (o: StoryResponse) =>
      Json.obj(
        "title" -> o.title,
        "commentIds" -> o.commentIds
      )

  def applyHttpResponse(httpResponse: HttpResponse)(implicit materializer: Materializer): Future[Option[StoryResponse]] =
    httpResponse
      .toJson
      .map(_.asOpt[StoryResponse])
}
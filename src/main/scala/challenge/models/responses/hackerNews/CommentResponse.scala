package challenge.models.responses.hackerNews

import challenge.models.hackerNews.User
import challenge.utils.HttpResponseUtils._
import challenge.utils.ThreadPools.sameThread

import play.api.libs.json._
import play.api.libs.functional.syntax._

import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer

import scala.concurrent.Future

case class CommentResponse(id: Long, user: User, subComments: Seq[Long])

object CommentResponse {
  implicit val reads: Reads[CommentResponse] =
    ((JsPath \ "id").read[Long] and (JsPath \ "by").read[String].map(User.apply(_)) and (JsPath \ "kids").read[Seq[Long]].orElse(Reads.pure(Nil)))(CommentResponse.apply _)

  implicit val writes: Writes[CommentResponse] =
    (o: CommentResponse) =>
      Json.obj(
        "id" -> o.id,
        "user" -> Json.toJson(o.user)
      )

  def applyHttpResponse(httpResponse: HttpResponse)(implicit materializer: Materializer): Future[Option[CommentResponse]] =
    httpResponse
      .toJson
      .map(_.asOpt[CommentResponse])
}
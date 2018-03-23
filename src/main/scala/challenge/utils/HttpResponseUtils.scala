package challenge.utils

import ThreadPools.sameThread

import play.api.libs.json.{ JsValue, Json }

import akka.http.scaladsl.model.HttpResponse
import akka.stream.Materializer
import akka.util.ByteString

import scala.concurrent.Future

object HttpResponseUtils {
  implicit class HttpResponse2Json(value: HttpResponse) {
    def toJson(implicit materializer: Materializer): Future[JsValue] =
      value
        .entity
        .dataBytes
        .runFold(ByteString(""))(_ ++ _)
        .map(_.utf8String)
        .map(Json.parse)
  }
}

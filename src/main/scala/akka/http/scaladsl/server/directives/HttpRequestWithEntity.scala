package akka.http.scaladsl.server.directives

import akka.http.scaladsl.model._

import scala.collection.immutable

class HttpRequestWithEntity[T](val body: T, val request: HttpRequest)

object HttpRequestWithEntity {
  def apply[T](
    body: T,
    method: HttpMethod = HttpMethods.GET,
    uri: Uri = Uri./,
    headers: immutable.Seq[HttpHeader] = Nil,
    entity: RequestEntity = HttpEntity.Empty,
    protocol: HttpProtocol = HttpProtocols.`HTTP/1.1`
  ): HttpRequestWithEntity[T] =
    new HttpRequestWithEntity(body, HttpRequest(method, uri, headers, entity, protocol))
}

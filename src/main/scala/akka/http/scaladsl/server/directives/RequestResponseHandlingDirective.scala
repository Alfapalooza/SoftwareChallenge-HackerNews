package akka.http.scaladsl.server.directives

import java.util.UUID

import challenge.logger.impl.{ ErrorLogger, RequestLogger }
import challenge.models.exceptions.ServiceResponseException
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import akka.http.scaladsl.model.{ HttpRequest, IdHeader }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive0, Directive1, ExceptionHandler, Route }

trait RequestResponseHandlingDirective extends PlayJsonSupport {
  def requestLogger: RequestLogger

  def errorLogger: ErrorLogger

  def requestResponseHandler(route: Route): Route =
    requestId {
      case (request, id) =>
        addRequestId(id) {
          addResponseId(id) {
            val start = System.currentTimeMillis
            tagAndLogRequest(request, id, start) {
              handleExceptions(exceptionHandler(request)) {
                route
              }
            }
          }
        }
    }

  private def exceptionHandler(req: HttpRequest): ExceptionHandler =
    ExceptionHandler {
      case exception: Exception =>
        val serviceResponseException =
          ServiceResponseException(exception)

        errorLogger.error(exception, req)
        complete(serviceResponseException.status, serviceResponseException)
    }

  private def requestId: Directive1[(HttpRequest, IdHeader)] =
    extractRequestContext.flatMap[Tuple1[(HttpRequest, IdHeader)]] { ctx =>
      provide(ctx.request -> ctx.request.header[IdHeader].getOrElse {
        val header = IdHeader(UUID.randomUUID.toString)
        header
      })
    }

  private def addRequestId(id: IdHeader): Directive0 =
    mapRequest { request =>
      request.copy(
        headers =
        id +:
          request.headers
      )
    }

  private def addResponseId(id: IdHeader): Directive0 =
    mapResponseHeaders { headers =>
      id +:
        headers
    }

  private def tagAndLogRequest(req: HttpRequest, id: IdHeader, start: Long): Directive0 =
    mapResponse { response =>
      requestLogger.request(id.value(), System.currentTimeMillis() - start, response.status.toString(), req)
      response
    }
}

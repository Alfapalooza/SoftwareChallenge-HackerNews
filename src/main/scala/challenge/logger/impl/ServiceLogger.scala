package challenge.logger.impl

import challenge.guice.Modules
import challenge.logger.{ Logger, LoggingInformation }
import challenge.services.{ NameForLogging, ServiceRequestResponse }

import com.google.inject.Inject

import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
import akka.http.scaladsl.server.directives.HttpRequestWithEntity

import scala.concurrent.{ ExecutionContext, Future }

class ServiceLogger(modules: Modules) extends Logger {
  override protected val logger: LoggingAdapter =
    Logging(modules.akka.actorSystem, getClass)

  def logService[T](
    service: NameForLogging,
    serviceRequest: HttpRequestWithEntity[T],
    originalRequest: HttpRequestWithEntity[_]
  )(fn: HttpRequestWithEntity[T] => Future[HttpResponse])(implicit executionContext: ExecutionContext, loggingInformation: LoggingInformation[ServiceRequestResponse[T]]): Future[HttpResponse] = {
    val start: Long = System.currentTimeMillis()

    fn(serviceRequest).map { serviceResponse =>
      info(
        ServiceRequestResponse(
          service,
          System.currentTimeMillis() - start,
          originalRequest,
          serviceRequest,
          serviceResponse
        )
      )
      serviceResponse
    }
  }
}

package challenge.models.exceptions

import challenge.models.{ DefaultServiceResponse, ServiceResponse }

import play.api.libs.json.Writes

class ServiceResponseException(override val msg: String, override val code: Int, override val status: Int) extends DefaultServiceResponse

object ServiceResponseException {
  def apply[T](serviceResponse: ServiceResponse[T])(implicit writes: Writes[T]): ServiceResponseException =
    new ServiceResponseException(serviceResponse.msg, serviceResponse.code, serviceResponse.status)

  def apply(throwable: Throwable): ServiceResponseException = {
    val msg =
      if (throwable.getMessage == null)
        "Cannot process exception message."
      else
        throwable.getMessage

    new ServiceResponseException(msg, 500, 500)
  }

  case object E0400 extends ServiceResponseException("Bad Request", 400, 400)

  case object E0500 extends ServiceResponseException("Internal Server Error", 500, 500)
}

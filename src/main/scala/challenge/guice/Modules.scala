package challenge.guice

import challenge.configuration.Configuration
import challenge.logger.impl.{ ApplicationLogger, ErrorLogger, RequestLogger, ServiceLogger }
import challenge.services.hackerNews.HackerNewsService

import com.google.inject.Inject

class Modules @Inject() (
    val configuration: Configuration,
    val akka: Akka
) {
  lazy val applicationLogger: ApplicationLogger =
    new ApplicationLogger(this)

  lazy val requestLogger: RequestLogger =
    new RequestLogger(this)

  lazy val serviceLogger: ServiceLogger =
    new ServiceLogger(this)

  lazy val errorLogger: ErrorLogger =
    new ErrorLogger(this)

  lazy val hackerNewsService: HackerNewsService =
    new HackerNewsService(this)
}

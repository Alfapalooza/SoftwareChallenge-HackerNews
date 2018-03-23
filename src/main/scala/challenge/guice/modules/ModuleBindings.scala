package challenge.guice.modules

import challenge.configuration.Configuration
import challenge.guice.{ Akka, Modules }
import challenge.logger.impl.{ ApplicationLogger, ErrorLogger, RequestLogger, ServiceLogger }
import challenge.services.hackerNews.HackerNewsService
import net.codingwell.scalaguice.ScalaModule

import com.google.inject.AbstractModule

class ModuleBindings extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[Akka].asEagerSingleton()
    bind[Configuration]
    bind[Modules]
  }
}

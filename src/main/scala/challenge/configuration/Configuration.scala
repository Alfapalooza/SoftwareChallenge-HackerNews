package challenge.configuration

import com.google.inject.Inject
import com.typesafe.config.{ Config, ConfigFactory }

import akka.util.Timeout

import scala.concurrent.duration._

class Configuration @Inject() () {
  lazy val underlyingConfig: Config =
    ConfigFactory.load().resolve()

  lazy val akkaConfiguration: Config =
    underlyingConfig.getConfig("akka.server")

  lazy val name: String =
    akkaConfiguration.getString("name")

  lazy val interface: String =
    akkaConfiguration.getString("interface")

  lazy val port: Int =
    akkaConfiguration.getInt("port")

  lazy val timeout: Timeout =
    Timeout(akkaConfiguration.getInt("timeout") seconds)
}

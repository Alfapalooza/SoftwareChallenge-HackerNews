lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"

lazy val dependencyInjectionLibraries =
  Seq(
    "com.google.inject.extensions" % "guice-assistedinject" % "4.1.0",
    "net.codingwell" %% "scala-guice" % "4.1.0")

lazy val jsonParsingLibrary =
  "com.typesafe.play" %% "play-json" % "2.6.9"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "challenge",
      scalaVersion    := "2.12.4"
    )),
    name := "softwarechallenge-hackernews",
    resolvers +=
      Resolver.bintrayRepo("hseeberger", "maven"),
    libraryDependencies ++=
      Seq(
        "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
        "de.heikoseeberger" %% "akka-http-circe"      % "1.20.0",

        "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
        "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
        "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
        "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test) ++ dependencyInjectionLibraries :+ jsonParsingLibrary
  )

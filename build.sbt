version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.13"

lazy val zioVersion = "1.0.4-2"
lazy val `zio-interop` = "2.3.1.0"
lazy val Http4sVersion = "0.21.19"

lazy val doobie = Seq( // Start with this one
  "org.tpolecat" %% "doobie-core"      % "0.8.4",
  "org.tpolecat" %% "doobie-postgres"  % "0.8.4", // Postgres driver 42.2.8 + type mappings.
  "org.tpolecat" %% "doobie-hikari"     % "0.8.4",
  "org.tpolecat" %% "doobie-specs2"    % "0.8.4" % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % "0.8.4" % "test"
)

lazy val zio = "dev.zio" %% "zio" % zioVersion
lazy val http4sBlazeServer =
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion
lazy val http4sDsl = "org.http4s" %% "http4s-dsl" % Http4sVersion
lazy val `zio-interop-cats` = "dev.zio" %% "zio-interop-cats" % `zio-interop`
lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.14.1"



lazy val model = (project in file("model"))


lazy val httpServer = (project in file("http-server"))
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    libraryDependencies ++= doobie,
    libraryDependencies ++= Seq(
      zio,
      http4sBlazeServer,
      http4sDsl,
      `zio-interop-cats`,
      logback,
      pureConfig
    )
  )
  .dependsOn(model)


libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1"
enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:postgresql://localhost/qcmio"
flywayUser := "qcmioUser"
flywayPassword := "qcmio"
flywayLocations += "db/migration"


lazy val myScalacOptions = Seq(
  "-feature",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-language:postfixOps",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ywarn-value-discard"
)

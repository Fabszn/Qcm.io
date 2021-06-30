import Dependencies._
version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.13.6"

lazy val model =
  (project in file("model")).settings(libraryDependencies ++= circle)
lazy val db = (project in file("db"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1",
    flywayUrl := "jdbc:postgresql://localhost/qcmio",
    flywayUser := "qcmio",
    flywayPassword := "qcmiopwd",
    flywayLocations += "db/migration"
  )

lazy val httpServer = (project in file("http-server"))
  .enablePlugins(JavaAppPackaging, DockerPlugin, DockerComposePlugin)
  .settings(
    libraryDependencies ++= circle,
    libraryDependencies ++= doobie,
    libraryDependencies ++= Seq(
      zio,
      http4sBlazeServer,
      http4sDsl,
      http4sCircle,
      `zio-interop-cats`,
      logback,
      pureConfig
    )
  )
  .dependsOn(model)

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

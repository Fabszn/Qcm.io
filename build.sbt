import Dependencies._
version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.13"




lazy val model = (project in file("model"))
lazy val db = (project in file("db"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1",
    flywayUrl := "jdbc:postgresql://localhost/qcmio",
    flywayUser := "qcmioUser",
    flywayPassword := "qcmio",
    flywayLocations += "db/migration"
  )

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
      pureConfig,
      quillJdbc
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

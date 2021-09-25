import Dependencies._

version := "1.0"



ThisBuild / scalaVersion  := "2.13.6"

lazy val model =
  (project in file("model")).settings(libraryDependencies ++= circle)
lazy val db = (project in file("db"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies += "org.postgresql" % "postgresql" % "42.2.23",
    flywayUrl                               := "jdbc:postgresql://localhost/qcmio",
    flywayUser                              := "qcmio",
    flywayPassword                          := "qcmiopwd",
    flywayLocations += "db/migration"
  )

lazy val front = (project in file("frontend"))
  .enablePlugins(ScalaJSWebjarPlugin,ScalaJSBundlerPlugin,ScalaJSWeb)
  .settings(
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % Version.scalaJsDom,
      "com.raquo" %%% "laminar" % Version.laminar,
      "com.github.japgolly.scalacss" %%% "core" % Version.scalaCss,
      "com.raquo" %%% "waypoint" % Version.waypoint,
      "com.lihaoyi" %%% "upickle" % Version.upickle,
    ),
    scalaJSUseMainModuleInitializer := true
  )

lazy val fronWebJar = front.webjar

lazy val http = (project in file("http-server"))
  .enablePlugins(SbtWeb,JavaAppPackaging, DockerPlugin, DockerComposePlugin)
  .settings(
    maintainer := "fabszn@protonmail.com",
    scalaJSProjects := Seq(front),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "scalatags" % "0.8.2",
      zio,
      http4sBlazeServer,
      http4sDsl,
      http4sCircle,
      `zio-interop-cats`,
      logback,
      pureConfig
    ) ++ circle ++ doobie
  )
  .dependsOn(model).dependsOn(fronWebJar)

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

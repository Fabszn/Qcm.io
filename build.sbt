import Dependencies._

version := "1.0"



ThisBuild / scalaVersion  := "2.13.6"

lazy val shared =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared")).settings(libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core" % Version.circe,
    "io.circe" %%% "circe-generic" % Version.circe,
    "io.circe" %%% "circe-parser" % Version.circe
  ))

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js


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
  ).dependsOn(shared.js)

lazy val fronWebJar = front.webjar

lazy val http = (project in file("http-server"))
  .enablePlugins(SbtWeb,JavaAppPackaging, DockerPlugin, DockerComposePlugin)

  .settings(
    maintainer := "fabszn@protonmail.com",
    packageName in Docker := "qcm.io",
    dockerUsername in Docker := Some("registry.gitlab.com/fabszn"),
    version in Docker := "latest",
    scalaJSProjects := Seq(front),
    Assets / pipelineStages := Seq(scalaJSPipeline),
    Compile / compile := ((Compile / compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      zio,
      http4sBlazeServer,
      http4sDsl,
      http4sCircle,
      `zio-interop-cats`,
      logback,
      pureConfig,
        scalaJwt,
      scalaTest,
      chimney
    ) ++ circle ++ doobie,
      addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )
  .dependsOn(shared.jvm,fronWebJar)

lazy val myScalacOptions = Seq(
  "-Ypartial-unification",
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

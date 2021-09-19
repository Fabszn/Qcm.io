import Dependencies._

version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.13.6"

lazy val model =
  (project in file("model")).settings(libraryDependencies ++= circle)
lazy val db = (project in file("db"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1",
    flywayUrl                               := "jdbc:postgresql://localhost/qcmio",
    flywayUser                              := "qcmio",
    flywayPassword                          := "qcmiopwd",
    flywayLocations += "db/migration"
  )

lazy val front = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % Version.scalaJsDom,
      "com.raquo" %%% "laminar" % Version.laminar,
      "com.github.japgolly.scalacss" %%% "core" % Version.scalaCss,
      "com.raquo" %%% "waypoint" % Version.waypoint,
      "com.lihaoyi" %%% "upickle" % Version.upickle,
    ),
    skip in packageJSDependencies := false,
    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv(),
    scalaJSUseMainModuleInitializer := true
  )

lazy val http = (project in file("http-server"))
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
    ),
    // Allows to read the generated JS on client
    resources in Compile += (fastOptJS in (front, Compile)).value.data,
    // Lets the backend to read the .map file for js
    resources in Compile += (fastOptJS in (front, Compile)).value
      .map((x: sbt.File) => new File(x.getAbsolutePath + ".map"))
      .data,
    // Lets the server read the jsdeps file
    (managedResources in Compile) += (artifactPath in (front, Compile)).value,
    // do a fastOptJS on reStart
    reStart := (reStart dependsOn (fastOptJS in (front, Compile))).evaluated,
    // This settings makes reStart to rebuild if a scala.js file changes on the client
    watchSources ++= (watchSources in front).value,
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

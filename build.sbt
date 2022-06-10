import Dependencies._

version := "1.0"



ThisBuild / scalaVersion  := "2.13.6"

lazy val myScalacOptions = Seq(
  "-Ywarn-unused:_",
  "-Ywarn-dead-code",
  "-deprecation"

)

lazy val qcmio = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    scalacOptions := myScalacOptions,
    maintainer := "Fabrice Sznajderman",
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
      chimney,
      flyway,
      postgresDriver
    ) ++ circle ++ quill,
      addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )



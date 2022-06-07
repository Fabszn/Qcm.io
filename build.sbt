import Dependencies.Version.flywayVersion
import Dependencies._

version := "1.0"



ThisBuild / scalaVersion  := "2.13.6"




/*lazy val db = (project in file("db"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies += "org.postgresql" % "postgresql" % "42.2.23",
   /* flywayUrl                               := "jdbc:postgresql://ec2-34-250-19-18.eu-west-1.compute.amazonaws.com/d5b2c9qv4lgkm",
    flywayUser                              := "tffifyrbukuzai",
    flywayPassword                          := "070c4efba10a37d0e04f056e3c3a4e6e9e7577fd54e28f97a91de5e543c09f23",*/
    flywayUrl                               := "jdbc:postgresql://localhost/qcmio",
    flywayUser                              := "qcmio",
    flywayPassword                          := "qcmiopwd",
    flywayLocations += "db/migration"
  )*/



lazy val myScalacOptions = Seq(
  "-Ywarn-unused:_",
  "-Ywarn-dead-code",
  "-deprecation"

)

lazy val http = (project in file("http-server"))
  .enablePlugins(SbtWeb,JavaAppPackaging, DockerPlugin, DockerComposePlugin)

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
      flyway
    ) ++ circle ++ doobie,
      addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
  )



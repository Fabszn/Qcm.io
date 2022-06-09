import Dependencies.Version._
import sbt._

object Dependencies {



  object Version {
    lazy val zioVersion = "1.0.13"
    lazy val `zio-interop` = "2.3.1.0"
    lazy val Http4sVersion = "0.23.10"
    lazy val quillVersion = "3.16.3"
    lazy val doobieVersion = "0.12.1"
    lazy val circe = "0.13.0"
    lazy val scalaJwt = "9.0.2"
    lazy val scalaTest = "3.2.10"
    lazy val chimneyVersion = "0.6.1"
    lazy val flywayVersion              = "7.4.0"
  }

  lazy val flyway = "org.flywaydb" % "flyway-core" % flywayVersion

  lazy val scalaJwt = "com.github.jwt-scala" %% "jwt-circe" % Version.scalaJwt

  lazy val circle = Seq(
    "io.circe" %% "circe-core" % Version.circe,
    "io.circe" %% "circe-generic" % Version.circe,
    "io.circe" %% "circe-parser" % Version.circe
  )


lazy val chimney = "io.scalaland" %% "chimney" % chimneyVersion

  lazy val zio = "dev.zio" %% "zio" % zioVersion
  lazy val http4sBlazeServer =
    "org.http4s" %% "http4s-blaze-server" % Http4sVersion
  lazy val http4sDsl = "org.http4s" %% "http4s-dsl" % Http4sVersion
  lazy val http4sCircle = "org.http4s" %% "http4s-circe" % Http4sVersion
  lazy val `zio-interop-cats` = "dev.zio" %% "zio-interop-cats" % `zio-interop`
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.14.1"

  lazy val quill = Seq(
    "io.getquill" %% "quill-jdbc-zio" % quillVersion,
    "io.getquill" %% "quill-zio"      % quillVersion,
    "io.getquill" %% "quill-jdbc"     % quillVersion,
    "io.getquill" %% "quill-sql"      % quillVersion,
    "io.getquill" %% "quill-core"     % quillVersion,
    "io.getquill" %% "quill-engine"   % quillVersion
  )


  lazy val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"

}

import Dependencies.Version._
import sbt._

object Dependencies {



  object Version {
    lazy val zioVersion = "1.0.4-2"
    lazy val `zio-interop` = "2.3.1.0"
    lazy val Http4sVersion = "0.21.19"
    lazy val quill = "3.6.1"
    lazy val doobieVersion = "0.12.1"
    lazy val circe = "0.13.0"
    lazy val scalaJsDom = "1.1.0"
    lazy val laminar = "0.13.1"
    lazy val scalaCss = "0.8.0-RC1"
  }

  lazy val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % doobieVersion,
    "org.tpolecat" %% "doobie-postgres" % doobieVersion, // Postgres driver 42.2.8 + type mappings.
    "org.tpolecat" %% "doobie-hikari" % doobieVersion,
    "org.tpolecat" %% "doobie-specs2" % doobieVersion % "test", // Specs2 support for typechecking statements.
    "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",
    "org.tpolecat" %% "doobie-quill" % doobieVersion
  )

  lazy val circle = Seq(
    "io.circe" %% "circe-core" % Version.circe,
    "io.circe" %% "circe-generic" % Version.circe,
    "io.circe" %% "circe-parser" % Version.circe
  )



  lazy val zio = "dev.zio" %% "zio" % zioVersion
  lazy val http4sBlazeServer =
    "org.http4s" %% "http4s-blaze-server" % Http4sVersion
  lazy val http4sDsl = "org.http4s" %% "http4s-dsl" % Http4sVersion
  lazy val http4sCircle = "org.http4s" %% "http4s-circe" % Http4sVersion
  lazy val `zio-interop-cats` = "dev.zio" %% "zio-interop-cats" % `zio-interop`
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.14.1"

  val quillJdbc = "io.getquill" %% "quill-jdbc" % Version.quill


}

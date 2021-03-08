import bloop.shaded.ch.epfl.scala.bsp4j.JavacOptionsParams

version := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.12.13"

lazy val zioVersion = "1.0.4-2"
lazy val `zio-interop` = "2.3.1.0"
lazy val Http4sVersion = "0.21.19"

lazy val zio = "dev.zio" %% "zio" %  zioVersion
lazy val http4sBlazeServer = "org.http4s"  %% "http4s-blaze-server" % Http4sVersion
lazy val http4sDsl = "org.http4s"      %% "http4s-dsl"          % Http4sVersion
lazy val `zio-interop-cats` = "dev.zio" %% "zio-interop-cats" % `zio-interop`



lazy val httpServer = (project in file("http-server"))
.enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings( libraryDependencies ++= Seq(
  zio,
  http4sBlazeServer,
  http4sDsl,
  `zio-interop-cats`,
)
)


lazy val myScalacOptions = Seq(
  "-feature",
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-language:postfixOps",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ywarn-value-discard",
)





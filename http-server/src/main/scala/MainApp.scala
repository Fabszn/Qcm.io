package http.server

import cats.data.Kleisli
import cats.effect.{ExitCode => CatsExitCode}
import org.http4s.{Request, Response}
import org.http4s.server.blaze.BlazeServerBuilder
import org.qcmio.environment.Environments.{AppEnvironment, appEnvironment}
import org.qcmio.environment.config.Configuration.HttpConf
import org.qcmio.environment.http._
import org.http4s.implicits._
import org.http4s.server.Router
import cats.implicits._

import zio.interop.catz._
import zio._

object QcmIOApp extends zio.App {

  type ServerRIO[A] = RIO[AppEnvironment, A]
  val program =
    for {
      server <- ZIO
                 .runtime[AppEnvironment]
                 .flatMap { implicit rts =>
                   val conf = rts.environment.get[HttpConf]
                   BlazeServerBuilder[ServerRIO](rts.platform.executor.asEC)
                     .bindHttp(conf.port, conf.host)
                     .withHttpApp(initRoutes)
                     .serve
                     .compile[ServerRIO, ServerRIO, CatsExitCode]
                     .drain
                 }
    } yield server

  def initRoutes: Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] = {
    val questionEndpoint = new QuestionsEndpoint[AppEnvironment].routes
    val adminEndpoint = new AdminEndpoint[AppEnvironment].routes
    val webJarsEndpoint = new StaticResourcesEndpoint[AppEnvironment].routes
    val indexEndpoint = new IndexEndpoint[AppEnvironment].routes

    val routes = adminEndpoint <+>
      questionEndpoint

    Router[ServerRIO](
      "qcm" -> routes,
                "assets" -> (webJarsEndpoint <+>  indexEndpoint)
    ).orNotFound

  }
  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program
      .provideSomeLayer(appEnvironment)
      .fold[ExitCode](_ => ExitCode.failure, _ => ExitCode.success)
}

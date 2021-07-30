package http.server

import cats.data.Kleisli
import cats.effect.{ExitCode => CatsExitCode}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.blaze.BlazeServerBuilder
import org.qcmio.environment.Environments.{AppEnvironment, appEnvironment}
import org.qcmio.environment.config.Configuration.HttpConf
import org.qcmio.environment.http.QuestionsEndpoint
import org.http4s.implicits._
import org.http4s.server.Router
import org.qcmio.environment.config.Configuration
import zio.blocking.Blocking
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
                     .withHttpApp(initRoutes("qcm"))
                     .serve
                     .compile[ServerRIO, ServerRIO, CatsExitCode]
                     .drain
                 }
    } yield server

  def initRoutes(
      rootPath: String
  ): Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] = {
    val questionEndpoint = new QuestionsEndpoint[AppEnvironment]

    val routes = questionEndpoint.routes
    Router[ServerRIO](rootPath -> routes).orNotFound

  }
  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program
      .provideSomeLayer(appEnvironment)
      .fold[ExitCode](_ => ExitCode.failure, _ => ExitCode.success)
}

package http.server

import cats.data.Kleisli
import cats.effect._
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.blaze.BlazeServerBuilder
import org.qcmio.environment.Environments.{AppEnvironment, appEnvironment}
import org.qcmio.environment.config.Configuration.HttpConf
import org.qcmio.environment.http.QuestionsEndpoint
import org.qcmio.environment.repository.QuestionRepository
import org.http4s.implicits._
import org.http4s.server.Router
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{ExitCode => ZExitCode, _}

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
            .compile[ServerRIO, ServerRIO, ExitCode]
            .drain
        }
    } yield server

  def initRoutes(rootPath:String): Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] =
{
  val questionEndpoint = new QuestionsEndpoint[AppEnvironment]

  val routes = questionEndpoint.routes
  Router[ServerRIO](rootPath -> routes).orNotFound


}
  def run(args: List[String]) =
    program
      .provideLayer(appEnvironment)
      .fold(_ => ZExitCode.failure, _ => ZExitCode.success)
}

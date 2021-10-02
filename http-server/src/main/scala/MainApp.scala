package http.server

import cats.data.Kleisli
import cats.effect.{Blocker, ExitCode => CatsExitCode}
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.staticcontent.WebjarService.Config
import org.http4s.server.staticcontent.{ResourceService, resourceService, webjarService}
import org.http4s.{Request, Response}
import org.qcmio.environment.Environments.{AppEnvironment, appEnvironment}
import org.qcmio.environment.config.Configuration.HttpConf
import org.qcmio.environment.http._
import zio._
import zio.internal.Executor
import zio.interop.catz._


object QcmIOApp extends zio.App {


  type ServerRIO[A] = RIO[AppEnvironment, A]
  val program =
    for {
      executors <- blocking.blockingExecutor
      server <- ZIO
        .runtime[AppEnvironment]
        .flatMap { implicit rts =>
          val conf = rts.environment.get[HttpConf]
          BlazeServerBuilder[ServerRIO](rts.platform.executor.asEC)
            .bindHttp(conf.port, conf.host)
            .withHttpApp(initRoutes(executors))
            .serve
            .compile[ServerRIO, ServerRIO, CatsExitCode]
            .drain
        }
    } yield server

  def initRoutes(exec:Executor): Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] = {
    val questionEndpoint = new QuestionsEndpoint[AppEnvironment].routes
    val adminEndpoint = new AdminEndpoint[AppEnvironment].routes
    val loginEndpoint = new LoginEndpoint[AppEnvironment].httpRoutes

    val routes = questionEndpoint <+> adminEndpoint <+> loginEndpoint


    Router[ServerRIO](
      "/api" -> routes,
      "qcm" -> resourceService[ServerRIO](ResourceService.Config("/assets", Blocker.liftExecutorService(exec.asECES))),
      "assets" -> {
        webjarService(
          Config(
            filter = _.asset.endsWith(".js"),
            blocker = Blocker.liftExecutorService(exec.asECES)
          )
          )
      }
    ).orNotFound

  }

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program
      .provideSomeLayer(appEnvironment)
      .fold[ExitCode](_ => ExitCode.failure, _ => ExitCode.success)
}

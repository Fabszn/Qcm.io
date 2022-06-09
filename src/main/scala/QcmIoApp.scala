package http.server

import cats.data.Kleisli
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.blaze.server.BlazeServerBuilder

import org.http4s.{Request, Response}
import org.qcmio.environment.Environments.{AppEnvironment, appEnvironment}
import org.qcmio.environment.config.config._
import org.qcmio.environment.domain.auth.AuthenticatedUser
import org.qcmio.environment.http._
import zio._
import zio.interop.catz._


object QcmIOApp extends zio.App {


  type ApiTask[A] = Task[A]
  val program =
    for {
      server <- getConf >>= (conf => ZIO
        .runtime[AppEnvironment]
        .flatMap { implicit rts =>
          BlazeServerBuilder[ApiTask]
            .bindHttp(conf.httpServer.port, conf.httpServer.host)
            .withHttpApp(qcmIoServices(conf.jwt))
            .serve
            .compile
            .drain
        })
    } yield server





  def qcmIoServices(conf:JwtConf): Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]] = {
    val authMiddleware: AuthMiddleware[ServerRIO, AuthenticatedUser] = AuthMiddleware[ServerRIO, AuthenticatedUser](authUser(conf))

    val examensEndpoint = new ExamensEndpoint[AppEnvironment].routes(authMiddleware)
    val adminEndpoint = new AdminEndpoint[AppEnvironment].routes
    val loginEndpoint = new LoginEndpoint[AppEnvironment](conf).httpRoutes

    val routes = {
      authMiddleware(
      questionApi.api <+> adminEndpoint <+> loginEndpoint <+> examensEndpoint
      )
    }


    Router[ServerRIO](
    /*"/api" -> routes
    "qcm" -> ???,
    "assets" -> ???*/
      }
    ).orNotFound

  }

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program
      .provideSomeLayer(appEnvironment)
      .fold[ExitCode](_ => ExitCode.failure, _ => ExitCode.success)


}

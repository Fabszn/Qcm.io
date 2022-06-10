package http.server

import cats.data.Kleisli
import cats.implicits._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{Request, Response}
import org.qcmio.environment.Environments.{AppEnvironment, appEnvironment}
import org.qcmio.environment.config.config._
import org.qcmio.environment.http._
import zio._
import zio.interop.catz._


object QcmIOApp extends zio.App {


  val program =
    ZIO.runtime[AppEnvironment].flatMap { _ =>

      getConf >>= (conf =>
        BlazeServerBuilder[ApiTask]
          .bindHttp(conf.httpServer.port, conf.httpServer.host)
          .withHttpApp(qcmIoServices(conf))
          .serve
          .compile
          .drain
        )
    }


  def qcmIoServices(conf: GlobalConfig): Kleisli[ApiTask, Request[ApiTask], Response[ApiTask]] = {
    val authMiddleware: AuthMiddleware[ApiTask, UserInfo] = AuthMiddleware[ApiTask, UserInfo](authUser(conf))


    val qcmServices =
      authMiddleware(
        questionApi.api <+> adminApi.api <+> examensAPI.api
      )


    Router[ApiTask](
      "/api" -> qcmServices,
      "/" -> loginApi.api

    ).orNotFound

  }

  def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program
      .provideSomeLayer(appEnvironment)
      .fold[ExitCode](_ => ExitCode.failure, _ => ExitCode.success)


}

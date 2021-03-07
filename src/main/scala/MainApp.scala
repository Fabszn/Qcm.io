package http.server

import cats.effect._
import fs2.Stream.Compiler._
import http.server.zhx.servers.Hello1Service
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio.interop.catz._
import zio.{ExitCode => ZExitCode, _}
import org.http4s.server.blaze._

object MyApp extends zio.App {




    val server: ZIO[ZEnv, Throwable, Unit] = ZIO.runtime[ZEnv]
      .flatMap {
        implicit rts =>
          BlazeServerBuilder.apply[Task](rts.platform.executor.asEC,)
            .bindHttp(8080, "localhost")
            .withHttpApp(Hello1Service.service)
            .serve
            .compile[Task,Task, ExitCode]
            .drain
      }

  def run(args: List[String]) =
    server.fold(_ =>  ZExitCode.failure , _ => ZExitCode.success)
}

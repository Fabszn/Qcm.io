package http.server

import cats.implicits._
import org.http4s.implicits._
import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._
import http.server.zhx.servers.Hello1Service
import fs2.Stream.Compiler._

import org.http4s.server.blaze._


object MyApp extends zio.App {




    val server: ZIO[ZEnv, Throwable, Unit] = ZIO.runtime[ZEnv]
      .flatMap {
        implicit rts =>
          BlazeServerBuilder[Task]
            .bindHttp(8080, "localhost")
            .withHttpApp(Hello1Service.service)
            .serve
            .compile
            .drain
      }

  def run(args: List[String]) =
    server.fold(_ =>  ExitCode.failure , _ => ExitCode.success)
}

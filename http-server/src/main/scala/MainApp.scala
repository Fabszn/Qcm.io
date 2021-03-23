package http.server

import cats.effect._
import org.http4s.server.blaze.BlazeServerBuilder
import org.qcmio.configuration.{Configuration, loadApi}
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{ExitCode => ZExitCode, _}

object MyApp extends zio.App {

  type AppEnvironment = Configuration
  val program =
    for {
      apiConf <- loadApi
      server <- ZIO
        .runtime[AppEnvironment]
        .flatMap { implicit rts =>
          BlazeServerBuilder
            .apply[Task](rts.platform.executor.asEC)
            .bindHttp(apiConf.port, apiConf.host)
            .withHttpApp(Hello1Service.service)
            .serve
            .compile[Task, Task, ExitCode]
            .drain
        }
    } yield server

  def run(args: List[String]) =
    program.provideSomeLayer[ZEnv](Configuration.live).fold(_ => ZExitCode.failure, _ => ZExitCode.success)
}

package http.server

import cats.effect._
import org.http4s.server.blaze.BlazeServerBuilder
import org.qcmio.environment.Environments.appEnvironment
import org.qcmio.environment.config
import org.qcmio.environment.config.Configuration
import org.qcmio.environment.config.Configuration.HttpConf
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{ExitCode => ZExitCode, _}

object MyApp extends zio.App {

  type AppEnvironment = Configuration
  val program =
    for {
      server <- ZIO
        .runtime[AppEnvironment]
        .flatMap { implicit rts =>
          val conf = rts.environment.get[HttpConf]
          BlazeServerBuilder
            .apply[Task](rts.platform.executor.asEC)
            .bindHttp(conf.port, conf.host)
            .withHttpApp(Hello1Service.service)
            .serve
            .compile[Task, Task, ExitCode]
            .drain
        }
    } yield server

  def run(args: List[String]) =
    program
      .provideLayer(appEnvironment)
      .fold(_ => ZExitCode.failure, _ => ZExitCode.success)
}

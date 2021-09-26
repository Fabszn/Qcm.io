package org.qcmio.environment.http

import cats.effect.Blocker
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import org.http4s.server.staticcontent._
import zio.RIO
import zio.interop.catz._

import java.util.concurrent.Executors

final class IndexEndpoint[R] {


  type IndexTask[A] = RIO[R, A]

  val blockingPool = Executors.newSingleThreadExecutor()
  val blocker = Blocker.liftExecutorService(blockingPool)
  val dsl = Http4sDsl[IndexTask]


  private val prefixPath = "/"


  import org.http4s._
  val routes: HttpRoutes[IndexTask] = Router(
    prefixPath -> {
      resourceService[IndexTask](ResourceService.Config("/assets", blocker))
    }
  )


}



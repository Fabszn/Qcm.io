package org.qcmio.environment.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.interop.catz._
import org.http4s.server.Router

final class AdminEndpoint[R] {

  val dsl: Http4sDsl[QCMTask] = Http4sDsl[QCMTask]

  import dsl._

  private val prefixPath = "/admin"
  private val httpRoutes: HttpRoutes[QCMTask] = HttpRoutes.of[QCMTask] {
    case GET -> Root => Ok("Ok  admin!")
  }

  val routes: HttpRoutes[QCMTask] = Router[QCMTask](
    prefixPath -> httpRoutes
  )

}

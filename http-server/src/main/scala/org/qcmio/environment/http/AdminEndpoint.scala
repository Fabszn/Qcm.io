package org.qcmio.environment.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.{IO, RIO, Task, UIO}
import zio.interop.catz._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.Router

final class AdminEndpoint[R] {

  val dsl: Http4sDsl[QTask] = Http4sDsl[QTask]

  import dsl._

private val prefixPath = "/admin"
  private val httpRoutes: HttpRoutes[QTask] = HttpRoutes.of[QTask] {
    case GET -> Root   => Ok("Ok  admin!")
  }

  val routes: HttpRoutes[QTask] = Router[QTask](
    prefixPath -> httpRoutes
  )





}

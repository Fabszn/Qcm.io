package org.qcmio.environment.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.{IO, RIO, Task, UIO}
import zio.interop.catz._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.Router

final class AdminEndpoint[R] {


  type AdminTask[A] = RIO[R, A]


  val dsl: Http4sDsl[AdminTask] = Http4sDsl[AdminTask]

  import dsl._

private val prefixPath = "/admin"
  private val httpRoutes: HttpRoutes[AdminTask] = HttpRoutes.of[AdminTask] {
    case GET -> Root   => Ok("Ok  admin!")
  }

  val routes: HttpRoutes[AdminTask] = Router[AdminTask](
    prefixPath -> httpRoutes
  )





}

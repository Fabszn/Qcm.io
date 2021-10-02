package org.qcmio.environment.http

import org.http4s.{HttpRoutes, Response}
import org.http4s.dsl.Http4sDsl
import zio.RIO
import org.http4s._, org.http4s.dsl.io._
import zio.interop.catz._

final class LoginEndpoint[R] {


  type LoginTask[A] = RIO[R, A]


  val dsl = Http4sDsl[LoginTask]

  import dsl._

  val httpRoutes = HttpRoutes.of[LoginTask] {
    case GET -> Root / "login"  => Ok("login")
  }






}



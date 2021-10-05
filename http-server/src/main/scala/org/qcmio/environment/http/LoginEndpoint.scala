package org.qcmio.environment.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.RIO
import org.qcmio.auth.User
import zio.interop.catz._
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.slf4j.LoggerFactory


final class LoginEndpoint[R] {
 val logger = LoggerFactory.getLogger("LoginEndpoint")

  type LoginTask[A] = RIO[R, A]


  val dsl = Http4sDsl[LoginTask]

  import dsl._

  val httpRoutes = HttpRoutes.of[LoginTask] {
    case req@POST -> Root / "login"  =>
      for {
        user <- req.as[User]
         _ = {logger.debug(s"login info ${user}")}
        rep <- Ok(s"Logged ${user.login}")
      }yield{
        rep
      }
  }






}



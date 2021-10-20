package org.qcmio.environment.http

import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Header, HttpRoutes}
import org.qcmio.Keys
import org.qcmio.auth.LoginInfo
import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.environment.http.jwt.JwtUtils
import org.qcmio.model.Candidat
import org.slf4j.LoggerFactory
import zio.interop.catz._
import zio.{RIO, Task}

final class LoginEndpoint[R](jwtConf: JwtConf) {
  private val logger = LoggerFactory.getLogger("LoginEndpoint")

  type LoginTask[A] = RIO[R, A]

  private val dsl = Http4sDsl[LoginTask]

  import dsl._

  val httpRoutes: HttpRoutes[LoginTask] = HttpRoutes.of[LoginTask] {
    case req @ POST -> Root / "login" =>
      for {
        info <- req.as[LoginInfo]
        _ = { logger.debug(s"login info ${info}") }
        rep <- Ok(s"Logged ${info.login}", Header(Keys.tokenHeader, JwtUtils.buildToken(Candidat.Email(info.login), jwtConf)))
      } yield {
        rep
      }
    case req @ GET -> Root / "login" / "isValid" =>
      for {
        tokenHeader <- Task(req.headers.get(CaseInsensitiveString(Keys.tokenHeader)))
        r <- tokenHeader.fold(Forbidden("No token found")) { h =>
          if (JwtUtils.isValidToken(h.value, jwtConf)) {
            Ok("token Ok")
          } else {
            Forbidden("Invalid Token")
          }
        }
      } yield {
        r
      }
  }

}

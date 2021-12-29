package org.qcmio.environment.http

import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString
import org.http4s.{Header, HttpRoutes}
import org.qcmio.Keys
import org.qcmio.auth.LoginInfo
import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.environment.http.jwt.JwtUtils
import org.qcmio.environment.repository.AdminRepository.admin
import org.qcmio.environment.repository.{AdministratorRepository, QuestionRepository}
import org.qcmio.model.User
import org.slf4j.LoggerFactory
import zio.interop.catz._
import zio.Task

final class LoginEndpoint[R <: QuestionRepository](jwtConf: JwtConf) {
  private val logger = LoggerFactory.getLogger("LoginEndpoint")

  private val dsl = Http4sDsl[QCMTask]

  import dsl._

  val httpRoutes = HttpRoutes.of[QCMTask] {
    case req @ POST -> Root / "login" =>
      for {
        info <- req.as[LoginInfo]
        user <- admin.authUser(info.login, info.mdp)
        rep <- user.fold(Forbidden("unAuthorized access")){user => Ok(s"Logged ", Header(Keys.tokenHeader, JwtUtils.buildToken(user.email, jwtConf)))}
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

package org.qcmio.environment.http

import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.qcmio.environment.domain.auth.LoginInfo
import org.qcmio.environment.repository.adminRepository.admin
import org.slf4j.LoggerFactory
import zio.interop.catz._

object loginApi {
  private val logger = LoggerFactory.getLogger("LoginEndpoint")

  private val dsl = Http4sDsl[ApiTask]

  import dsl._

  val api = HttpRoutes.of[ApiTask] {
    case req @ POST -> Root / "login" =>
      for {
        info <- req.as[LoginInfo]
        user <- admin.authUser(info.login, info.mdp)
        rep <- Ok(user)
      } yield {
        rep
      }

  }

}

package org.qcmio.environment.http

import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.qcmio.environment.domain.auth.AuthenticatedUser
import zio.interop.catz._

object adminApi {

  val dsl: Http4sDsl[ApiTask] = Http4sDsl[ApiTask]

  import dsl._


 val api: AuthedRoutes[UserInfo, ApiTask] = AuthedRoutes.of[UserInfo,ApiTask] {
    case GET -> Root as user => Ok("Ok  admin!")
  }



}

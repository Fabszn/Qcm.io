package org.qcmio.environment.http

import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import org.qcmio.environment.domain.auth.AuthenticatedUser
import zio.interop.catz._

object AdminApi {

  val dsl: Http4sDsl[QCMTask] = Http4sDsl[QCMTask]

  import dsl._


 def api: AuthedRoutes[AuthenticatedUser, QCMTask] = AuthedRoutes.of[AuthenticatedUser,QCMTask] {
    case GET -> Root as user => Ok("Ok  admin!")
  }



}

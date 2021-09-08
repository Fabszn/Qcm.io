package org.qcmio.environment.http

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.middleware._
import zio.{RIO, ZEnv}
import zio.interop.catz._

final class AdminEndpoint[R] {


  type AdminTask[A] = RIO[R, A]


  val dsl = Http4sDsl[AdminTask]

  import dsl._

  private val prefixPath = "/admin"

  private val httpRoutes = CORS(HttpRoutes.of[AdminTask] {
    case GET -> Root => Ok("Ok!")
  })


  val routes: HttpRoutes[AdminTask] = Router(
    prefixPath -> httpRoutes
  )


}

package org.qcmio.environment.http

import io.circe.generic.auto._
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.qcmio.auth.AuthenticatedUser
import org.qcmio.environment.repository.ExamenRepository
import org.qcmio.environment.repository.ExamenRepository.examen
import org.qcmio.model.Examen
import zio.interop.catz._

import scala.util.Try


final class ExamensEndpoint[R <: ExamenRepository] {

  val dsl = Http4sDsl[QCMTask]

  import dsl._

  private val prefixPath = "/examens"

  object ExamenIdVar {
    def unapply(s: String): Option[Examen.Id] = {
      if (s.nonEmpty)
        Try(Examen.Id(s.toLong)).toOption
      else
        None
    }
  }

  private val httpRoutes = AuthedRoutes.of[AuthenticatedUser, QCMTask] {
    case GET -> Root / ExamenIdVar(id) as user =>
      for {
        e <- Ok("")
      } yield e
    case authReq@POST -> Root  as user =>
      for {
        e <- authReq.req.as[Examen]
        idExamen <- examen.createExam(e)
        e <- Created(idExamen)
      } yield e


  }

  def routes(middleware: AuthMiddleware[QCMTask, AuthenticatedUser]): HttpRoutes[QCMTask] = Router(
    prefixPath -> middleware(httpRoutes)
  )


}



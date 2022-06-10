package org.qcmio.environment.http

import io.circe.generic.auto._
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.qcmio.environment.domain.auth.AuthenticatedUser
import org.qcmio.environment.domain.model.Examen
import org.qcmio.environment.repository.examenRepository.examen
import zio.interop.catz._

import scala.util.Try


object examensAPI {

  val dsl = Http4sDsl[ApiTask]

  import dsl._



  object ExamenIdVar {
    def unapply(s: String): Option[Examen.Id] = {
      if (s.nonEmpty)
        Try(Examen.Id(s.toLong)).toOption
      else
        None
    }
  }

  val api = AuthedRoutes.of[UserInfo, ApiTask] {
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




}



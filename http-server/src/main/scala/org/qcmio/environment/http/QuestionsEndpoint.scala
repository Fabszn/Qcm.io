package org.qcmio.environment.http

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.qcmio.auth.AuthenticatedUser
import org.qcmio.environment.repository.QuestionRepository
import org.qcmio.environment.repository.QuestionsRepository.{question, reponse}
import org.qcmio.model
import org.qcmio.model.{HttpReponse, Question}
import zio.RIO
import zio.interop.catz._

import scala.util.Try


final class QuestionsEndpoint[R <: QuestionRepository] {

  type Task[A] = RIO[R, A]


  val dsl = Http4sDsl[Task]

  import dsl._

  private val prefixPath = "/questions"

  object QuestionIdVar {
    def unapply(s: String): Option[Question.Id] = {
      if (s.nonEmpty)
        Try(Question.Id(s.toLong)).toOption
      else
        None
    }
  }

  private val httpRoutes = AuthedRoutes.of[AuthenticatedUser,Task] {
    case GET -> Root / QuestionIdVar(id) as user=>
      question.getQuestion(id) >>=  {
        case Some(e) => Ok(e)
        case None => NotFound()
      }
    case authReq@POST -> Root  / "reponse" as user =>
      for {
        rep <- authReq.req.as[HttpReponse]
          .map(hr => model.Reponse(label=hr.label,questionId=hr.idQuestion, isCorrect=hr.isCorrect ))
        res <- reponse.saveReponse(rep)
        json <- Created(res.asJson)
      } yield json
    case authReq@POST -> Root as user =>
      for {
        lbl <- authReq.req.as[Question.Label]
        res <- question.saveQuestion(lbl)
        json <- Created(res.asJson)
      } yield json
    case authReq@PUT -> (Root / QuestionIdVar(id)) as user=>
      for {
        lbl <- authReq.req.as[Question.Label]
        res <- question.updateQuestion(id,lbl)
        json <- Created(res.asJson)
      } yield json
  }

  def routes(middleware:AuthMiddleware[Task, AuthenticatedUser]): HttpRoutes[Task] = Router(
    prefixPath -> middleware(httpRoutes)
  )



}



package org.qcmio.environment.http

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.qcmio.auth.User
import org.qcmio.environment.repository.QuestionRepository
import org.qcmio.environment.repository.QuestionsRepository.question
import org.qcmio.model.Question
import zio.{RIO, Task}
import zio.interop.catz._

import scala.util.Try


final class QuestionsEndpoint[R <: QuestionRepository] {

  type QuestionTask[A] = RIO[R, A]


  val dsl = Http4sDsl[QuestionTask]

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

  private val httpRoutes = AuthedRoutes.of[User,QuestionTask] {
    case GET -> Root / QuestionIdVar(id) as user=>
      question.getQuestion(id) >>=  {
        case Some(e) => Ok(e)
        case None => NotFound()
      }
    case authReq@POST -> Root as user =>
      for {
        lbl <- authReq.req.as[Question.Label]
        res <- question.saveQuestion(lbl)
        json <- Created(res.asJson)
      } yield json
    case authReq@PUT -> Root / QuestionIdVar(id) as user=>
      for {
        lbl <- authReq.req.as[Question.Label]
        res <- question.updateQuestion(id,lbl)
        json <- Created(res.asJson)
      } yield json
  }

  def routes(middleware:AuthMiddleware[QuestionTask, User]): HttpRoutes[QuestionTask] = Router(
    prefixPath -> middleware(httpRoutes)
  )

}

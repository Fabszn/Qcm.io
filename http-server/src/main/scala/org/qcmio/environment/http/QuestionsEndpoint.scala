package org.qcmio.environment.http

import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.qcmio.environment.repository.QuestionRepository
import org.qcmio.environment.repository.QuestionsRepository.question
import org.qcmio.model.Question
import zio.RIO
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

  private val httpRoutes = HttpRoutes.of[QuestionTask] {
    case GET -> Root / QuestionIdVar(id) =>
      question.getQuestion(id) >>=  {
        case Some(e) => Ok(e)
        case None => NotFound()
      }
    case req@POST -> Root =>
      for {
        lbl <- req.as[Question.Label]
        res <- question.saveQuestion(lbl)
        json <- Created(res.asJson)
      } yield json
    case req@PUT -> Root / QuestionIdVar(id) =>
      for {
        lbl <- req.as[Question.Label]
        res <- question.updateQuestion(id,lbl)
        json <- Created(res.asJson)
      } yield json
  }

  val routes: HttpRoutes[QuestionTask] = Router(
    prefixPath -> httpRoutes
  )

}

package org.qcmio.environment.http

import org.http4s.{EntityDecoder, HttpRoutes}
import io.circe.syntax._
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.qcmio.environment.repository.{QuestionRepository, question}
import org.qcmio.model.Question
import zio.{RIO, Task}
import zio.interop.catz._

import scala.util.Try


final class QuestionsEndpoint[R <: QuestionRepository] {

  type QuestionsIO[A] = RIO[R, A]

  val dsl = Http4sDsl[QuestionsIO]
  import dsl._

  private val prefixPath = "/questions"

  object QuestionIdVar{
    def unapply(s:String):Option[Question.Id] = {
      if (s.nonEmpty)
        Try(Question.Id(s.toLong)).toOption
      else
        None
    }
  }
  implicit val decoder = jsonOf[Task, Question.Label]

  private val httpRoutes = HttpRoutes.of[QuestionsIO] {
    case GET -> Root / QuestionIdVar(id) =>
      for {
        q <- question.getQuestion(id)
      }yield q.map(q => Ok(q.asJson)) <> NotFound("Question not found")
    case req @ POST -> Root  =>
      for {
        lbl <- req.as[Question.Label]
        res  <-  question.saveQuestion(lbl)
        json <- Ok(res.map(_.asJson))
      } yield json
  }

  val routes: HttpRoutes[QuestionsIO] = Router(
    prefixPath -> httpRoutes
  )

}

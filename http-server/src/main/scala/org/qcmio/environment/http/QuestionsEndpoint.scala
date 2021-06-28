package org.qcmio.environment.http

import org.http4s.HttpRoutes
import io.circe.syntax._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.qcmio.environment.repository.{QuestionRepository, saveQuestion}
import org.qcmio.model.Question
import zio.{RIO, Task}
import zio.interop.catz._

final class QuestionsEndpoint[R <: QuestionRepository] {

  type QuestionsIO[A] = RIO[R, A]

  val dsl = Http4sDsl[QuestionsIO]
  import dsl._

  private val prefixPath = "/questions"

  private val httpRoutes = HttpRoutes.of[QuestionsIO] {
    case GET -> Root =>
    val r: RIO[QuestionRepository, Task[Long]] = saveQuestion(Question(Question.Id(1), Question.Label("question 1")))//.flatMap(qId => Ok(qId.asJson))
      for{
        res <- r
        json <- Ok(res.map(_.asJson))
      }yield json
  }

  val routes: HttpRoutes[QuestionsIO] = Router(
    prefixPath -> httpRoutes
  )

}

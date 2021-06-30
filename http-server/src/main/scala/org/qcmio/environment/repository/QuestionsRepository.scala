package org.qcmio.environment.repository

import org.qcmio.model.Question
import zio.{Task, URLayer, ZLayer}
import doobie.Transactor
import doobie.quill.DoobieContext
import io.getquill.SnakeCase
import org.qcmio.model
import doobie.implicits._
import zio.interop.catz._

object QuestionsRepository {

  trait Service {
    val xa: Transactor[Task]
    def saveQuestion(question: Question): Task[Long]
  }

  private[repository] final case class QuestionsRepository(xa: Transactor[Task])
      extends Service with DBContext {

    import ctx._

    def saveQuestion(question: model.Question): Task[Long] = run(quote {
      querySchema[Question]("t_question", _.id -> "pkid_question").insert(
        lift(question)
      )
    }).transact(xa)
  }
  val live: URLayer[DbTransactor, QuestionRepository] = ZLayer.fromService {
    resource: DbTransactor.Resource =>
      QuestionsRepository(resource.xa)
  }

}

package org.qcmio.environment.repository

import org.qcmio.model.Question
import zio.{Task, URLayer, ZLayer}
import doobie.Transactor
import doobie.implicits._
import zio.interop.catz._

object QuestionsRepository {

  val live: URLayer[DbTransactor, QuestionRepository] = ZLayer.fromService { resource: DbTransactor.Resource =>
    QuestionsRepository(resource.xa)
  }

  trait Service {
    val xa: Transactor[Task]

    def saveQuestion(label: Question.Label): Task[Long]
    def getQuestion(id:Question.Id):Task[Option[Question]]
  }

  private[repository] final case class QuestionsRepository(xa: Transactor[Task]) extends Service with DBContext {

    import ctx._


    def getQuestion(id: Question.Id): Task[Option[Question]] =
      run(quote(questionTable.filter(_.id == lift(id)))).transact(xa).map(_.headOption)

    def saveQuestion(label: Question.Label): Task[Long] =
      run(quote(nextId)).transact(xa) >>= save(label)

    private def save(label:Question.Label)(id:Question.Id): Task[Long] =
      run(quote {
        questionTable.insert(lift(Question(id, label)))
      }).transact(xa)


  }

}

package org.qcmio.environment.repository

import doobie.implicits._
import org.qcmio.model.Question
import zio.interop.catz._
import zio.{Task, URLayer, ZLayer}

object QuestionsRepository {

  val live: URLayer[DbTransactor, QuestionRepository] = ZLayer.fromService {
    QuestionsRepository
  }

  trait Service {
    val resource: DbTransactor.Resource

    def saveQuestion(label: Question.Label): Task[Long]

    def getQuestion(id: Question.Id): Task[Option[Question]]
  }

  private[repository] final case class QuestionsRepository(resource: DbTransactor.Resource) extends Service with DBContext {

    import ctx._
    import resource._

    def getQuestion(id: Question.Id): Task[Option[Question]] = {
      run(quote(questionTable.filter(_.id == lift(id)))).transact(xa).map(_.headOption)
    }

    def saveQuestion(label: Question.Label): Task[Long] =
      run(quote(nextId)).transact(xa) >>= save(label)

    private def save(label: Question.Label)(id: Question.Id): Task[Long] =
      run(quote {
        questionTable.insert(lift(Question(id, label)))
      }).transact(xa)


  }

}

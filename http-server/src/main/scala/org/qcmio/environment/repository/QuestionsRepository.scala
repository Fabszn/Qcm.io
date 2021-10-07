package org.qcmio.environment.repository

import doobie.implicits._
import org.qcmio.model.Question
import zio.interop.catz._
import zio.{RIO, Task, URLayer, ZLayer}

object QuestionsRepository {

  val live: URLayer[DbTransactor, QuestionRepository] = ZLayer.fromService {
    QuestionsRepository
  }

  trait Service {
    val resource: DbTransactor.Resource

    def saveQuestion(label: Question.Label): Task[Long]

    def getQuestion(id: Question.Id): Task[Option[Question]]

    def updateQuestion(id: Question.Id, label: Question.Label): Task[Long]
  }

  object question {
    def saveQuestion(q: Question.Label): RIO[QuestionRepository, Long] =
      RIO.accessM(_.get.saveQuestion(q))
    def getQuestion(id: Question.Id): RIO[QuestionRepository, Option[Question]] =
      RIO.accessM(_.get.getQuestion(id))
    def updateQuestion(id: Question.Id, label:Question.Label): RIO[QuestionRepository, Long] =
      RIO.accessM(_.get.updateQuestion(id, label))
  }

  private[repository] final case class QuestionsRepository(resource: DbTransactor.Resource) extends Service with DBContext {

    import ctx._
    import resource._

    def getQuestion(id: Question.Id): Task[Option[Question]] = {
      run(quote(questionTable.filter(_.id == lift(id)))).transact(xa).map(_.headOption)
    }

    override def updateQuestion(id: Question.Id, label: Question.Label): Task[Long] =
      run(quote(questionTable.filter(q => q.id == lift(id)).update(_.label -> lift(label)))).transact(xa)

    def saveQuestion(label: Question.Label): Task[Long] =
      run(quote(nextQuestionId)).transact(xa) >>= save(label)

    private def save(label: Question.Label)(id: Question.Id): Task[Long] =
      run(quote {
        questionTable.insert(lift(Question(id, label)))
      }).transact(xa)


  }

}

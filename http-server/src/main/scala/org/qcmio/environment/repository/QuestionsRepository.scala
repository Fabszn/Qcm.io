package org.qcmio.environment.repository

import doobie.implicits._
import org.qcmio.model.{Question, Reponse}
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

    def saveReponse(reponse: Reponse): Task[Long]

    def getReponse(id: Reponse.Id): Task[Option[Reponse]]

    def updateReponse(id: Reponse.Id, reponse: Reponse): Task[Long]

  }

  object question {
    def saveQuestion(q: Question.Label): RIO[QuestionRepository, Long] =
      RIO.accessM(_.get.saveQuestion(q))

    def getQuestion(id: Question.Id): RIO[QuestionRepository, Option[Question]] =
      RIO.accessM(_.get.getQuestion(id))

    def updateQuestion(id: Question.Id, label: Question.Label): RIO[QuestionRepository, Long] =
      RIO.accessM(_.get.updateQuestion(id, label))
  }

  object reponse {
    def saveReponse(r: Reponse): RIO[QuestionRepository, Long] =
      RIO.accessM(_.get.saveReponse(r))

    def getReponse(id: Reponse.Id): RIO[QuestionRepository, Option[Reponse]] =
      RIO.accessM(_.get.getReponse(id))

    def updateReponse(id: Reponse.Id, reponse: Reponse): RIO[QuestionRepository, Long] =
      RIO.accessM(_.get.updateReponse(id, reponse))
  }

  private[repository] final case class QuestionsRepository(resource: DbTransactor.Resource) extends Service with DBContext {

    import ctx._
    import resource._

    def getQuestion(id: Question.Id): Task[Option[Question]] = {
      (for {
       question <-  run(quote(questionTable.filter(_.id == lift(id)))).map(_.headOption)
       reponses <- question.fold(Task(List.empty[Reponse]))(q =>
         run(quote(reponseTable.filter(_.questionId == lift(q.id))))
       )

      }yield question).transact(xa)
    }

    override def updateQuestion(id: Question.Id, label: Question.Label): Task[Long] =
      run(quote(questionTable.filter(q => q.id == lift(id)).update(_.label -> lift(label)))).transact(xa)

    def saveQuestion(label: Question.Label): Task[Long] =
      (for {
        idQuestion <- run(nextQuestionId)
        nbLin <- run(quote {
          questionTable.insert(lift(Question(idQuestion, label)))
        })
      } yield nbLin).transact(xa)


    override def saveReponse(reponse: Reponse): Task[Long] =
      run(nextReponseId).transact(xa) >>= (idR => run(quote {
        reponseTable.insert(lift(reponse.copy(id = idR)))
      }).transact(xa))

    override def getReponse(id: Reponse.Id): Task[Option[Reponse]] =
      run(quote(reponseTable.filter(_.id == lift(id)))).transact(xa).map(_.headOption)

    override def updateReponse(id: Reponse.Id, reponse: Reponse): Task[Long] = ???


  }

}

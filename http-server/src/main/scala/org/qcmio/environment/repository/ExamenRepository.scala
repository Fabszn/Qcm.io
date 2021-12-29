package org.qcmio.environment.repository

import doobie.implicits._
import org.qcmio.model.{Examen, Question, User, UserReponse}
import zio.interop.catz._
import zio.{RIO, Task, URLayer, ZLayer}

object ExamenRepository {

  val live: URLayer[DbTransactor, ExamenRepository] = ZLayer.fromService {
    ExamensRepository
  }

  trait Service {
    val resource: DbTransactor.Resource

    def createExam(exam: Examen): Task[Long]

    def getExamen(id: Examen.Id): Task[Examen]

    def addQuestionToExam(idQuestion: Question.Id, idExamen: Examen.Id): Task[Long]

    def addAttendeeToExamen(idUser: User.Id, idExamen: Examen.Id): Task[Long]

    def addReponseToQuestion(reponse: UserReponse): Task[Long]

  }

  private[repository] final case class ExamensRepository(resource: DbTransactor.Resource)
      extends Service
      with DBContext {

    import ctx._
    import resource._

    override def createExam(exam: Examen): Task[Long] =
      (for {
        idExamen <- run(nextExamenId)
        nbLin <- run(quote {
                  examenTable.insert(lift(exam.copy(id = idExamen)))
                })
      } yield nbLin).transact(xa)

    override def getExamen(id: Examen.Id): Task[Examen] = ???

    override def addQuestionToExam(idQuestion: Question.Id, idExamen: Examen.Id): Task[Long] = ???

    override def addAttendeeToExamen(idUser: User.Id, idExamen: Examen.Id): Task[Long] = ???

    override def addReponseToQuestion(reponse: UserReponse): Task[Long] = ???
  }

  object examen {

    def createExam(exam: Examen): RIO[ExamenRepository, Long] =
      RIO.accessM(_.get.createExam(exam))

  }

}

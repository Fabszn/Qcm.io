package org.qcmio.environment.repository

import org.qcmio.environment.domain.model._
import zio._

import javax.sql.DataSource

object examenRepository {

  val layer: URLayer[Has[DataSource], Has[ExamenRepository]] = (
    ExamensRepository(_)
    ).toLayer

  trait ExamenRepository {


    def createExam(exam: Examen): Task[Long]

    def getExamen(id: Examen.Id): Task[Examen]

    def addQuestionToExam(idQuestion: Question.Id, idExamen: Examen.Id): Task[Long]

    def addAttendeeToExamen(idUser: User.Id, idExamen: Examen.Id): Task[Long]

    def addReponseToQuestion(reponse: UserReponse): Task[Long]

  }

  private[repository] final case class ExamensRepository(dataSource: DataSource)
    extends ExamenRepository {

    import QuillContext._

    val env = Has(dataSource)

    override def createExam(exam: Examen): Task[Long] =
      (for {
        idExamen <- run(nextExamenId)
        nbLin <- run(quote {
          examenTable.insert(_.id -> lift(idExamen),
            _.label -> lift(exam.label),
            _.date -> lift(exam.date),
          )
        })
      } yield nbLin).provide(env)

    override def getExamen(id: Examen.Id): Task[Examen] = ???

    override def addQuestionToExam(idQuestion: Question.Id, idExamen: Examen.Id): Task[Long] = ???

    override def addAttendeeToExamen(idUser: User.Id, idExamen: Examen.Id): Task[Long] = ???

    override def addReponseToQuestion(reponse: UserReponse): Task[Long] = ???
  }

  object examen {

    def createExam(exam: Examen): RIO[Has[ExamenRepository], Long] = ZIO.serviceWith[ExamenRepository](_.createExam(exam))

  }

}

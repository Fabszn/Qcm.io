package org.qcmio.environment.repository

import cats.data.OptionT
import org.qcmio.environment.domain.model._
import zio._
import zio.interop.catz._

import javax.sql.DataSource

object questionsRepository {

  val layer: RLayer[Has[DataSource], Has[QuestionRepository]] = (QuestionsRepository(_)).toLayer


  trait QuestionRepository {


    def saveQuestion(label: Question.Label): Task[Long]

    def getQuestion(id: Question.Id): Task[Option[Question]]

    def getQuestions: Task[Seq[Question]]

    def updateQuestion(id: Question.Id, label: Question.Label): Task[Long]

    def saveReponse(reponse: Reponse): Task[Long]

    def getReponse(id: Reponse.Id): Task[Option[Reponse]]

    def updateReponse(id: Reponse.Id, reponse: Reponse): Task[Long]

    def getReponsesByQuestionId(id: Question.Id): Task[Seq[Reponse]]

    def getQuestionReponses(id: Question.Id): Task[Option[QR]]

    def getAllQuestionsReponses: Task[Seq[QR]]

  }

  object question {
    /* def saveQuestion(q: Question.Label): RIO[QuestionRepository, Long] =
       RIO.accessM(_.get.saveQuestion(q))

     def getQuestion(id: Question.Id): RIO[QuestionRepository, Option[Question]] =
       RIO.accessM(_.get.getQuestion(id))

     def getQuestions: RIO[QuestionRepository, Seq[Question]] =
       RIO.accessM(_.get.getQuestions)

     def updateQuestion(id: Question.Id, label: Question.Label): RIO[QuestionRepository, Long] =
       RIO.accessM(_.get.updateQuestion(id, label))

     def getReponsesByQuestionId(id: Question.Id): RIO[QuestionRepository, Seq[Reponse]] =
       RIO.accessM(_.get.getReponsesByQuestionId(id))

     def getQuestionReponses(id: Question.Id): RIO[QuestionRepository, Option[QR]] =
       RIO.accessM(_.get.getQuestionReponses(id))

     def getAllQuestionsReponses: RIO[QuestionRepository, Seq[QR]] =
       RIO.accessM(_.get.getAllQuestionsReponses)
       */

  }

  object reponse {
    /* def saveReponse(r: Reponse): RIO[QuestionRepository, Long] =
       RIO.accessM(_.get.saveReponse(r))

     def getReponse(id: Reponse.Id): RIO[QuestionRepository, Option[Reponse]] =
       RIO.accessM(_.get.getReponse(id))

     def updateReponse(id: Reponse.Id, reponse: Reponse): RIO[QuestionRepository, Long] =
       RIO.accessM(_.get.updateReponse(id, reponse))*/

  }

  private[repository] final case class QuestionsRepository(dataSource: DataSource) extends QuestionRepository {

    import QuillContext._

    val env = Has(dataSource)

    def getQuestion(id: Question.Id): Task[Option[Question]] =
      (for {
        question <- run(quote(questionTable.filter(_.id == lift(id)))).map(_.headOption)
      } yield question) provide (env)

    def getQuestionReponses(id: Question.Id): Task[Option[QR]] =
      (for {
        question <- OptionT(run(quote(questionTable.filter(_.id == lift(id)))).map(_.headOption))
        reponses <- OptionT.liftF(run(quote(reponseTable.filter(_.questionId == lift(question.id)))))
      } yield (question, reponses)).value.provide(env)

    def getAllQuestionsReponses: Task[Seq[QR]] =
      (for {
        questions <- run(quote(questionTable))
        reponses <- run(quote(reponseTable))
      } yield {
        questions.map(q =>
          (q, reponses.groupBy(_.questionId).getOrElse(q.id, List.empty[Reponse])))
      }).provide(env)

    def getReponsesByQuestionId(id: Question.Id): Task[Seq[Reponse]] =
      (for {
        reps <- run(quote(reponseTable.filter(_.questionId == lift(id))))
      } yield reps).provide(env)


    override def updateQuestion(id: Question.Id, label: Question.Label): Task[Long] =
      run(quote(questionTable.filter(q => q.id == lift(id)).update(_.label -> lift(label)))).provide(env)

    override def saveQuestion(label: Question.Label): Task[Long] =
      (for {
        idQuestion <- run(nextQuestionId)
        nbLin <- run(quote {
          questionTable.insert(_.id -> lift(idQuestion), _.label -> lift(label))
        })
      } yield nbLin).provide(env)


    override def saveReponse(reponse: Reponse): Task[Long] =
      run(nextReponseId).provide(env) >>= (idR => run(quote {
        reponseTable.insert(_.id -> lift(idR),
          _.label -> lift(reponse.label),
          _.questionId -> lift(reponse.questionId),
          _.isCorrect -> lift(reponse.isCorrect)
        )
      }).provide(env))

    override def getReponse(id: Reponse.Id): Task[Option[Reponse]] =
      run(quote(reponseTable.filter(_.id == lift(id)))).provide(env).map(_.headOption)

    override def updateReponse(id: Reponse.Id, reponse: Reponse): Task[Long] = ???

    override def getQuestions: Task[Seq[Question]] = run(quote(questionTable)).provide(env)
  }

}

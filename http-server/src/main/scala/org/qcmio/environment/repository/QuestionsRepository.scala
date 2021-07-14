package org.qcmio.environment.repository

import doobie.Transactor
import doobie.hikari.HikariTransactor
import org.qcmio.model.Question
import zio.{RIO, Task, URLayer, ZIO, ZLayer, ZManaged, blocking}
import doobie.implicits._
import zio.blocking.Blocking
import zio.interop.catz._

object QuestionsRepository {

  val live: URLayer[DbTransactor, QuestionRepository] = ZLayer.fromService{QuestionsRepository}

  trait Service {
    val resource: DbTransactor.Resource

    def saveQuestion(label: Question.Label):RIO[Blocking, Long]
    def getQuestion(id:Question.Id):RIO[Blocking, Option[Question]]
  }

  private[repository] final case class QuestionsRepository(resource: DbTransactor.Resource) extends Service with DBContext {

    import ctx._

    def getQuestion(id: Question.Id): RIO[Blocking, Option[Question]] = {
      resource.xa.use { xa =>
        run(quote(questionTable.filter(_.id == lift(id)))).transact(xa).map(_.headOption)
      }
    }

    def saveQuestion(label: Question.Label): RIO[Blocking, Long] =
      resource.xa.use { implicit xa: Transactor[Task] =>
        run(quote(nextId)).transact(xa) >>= save(label)
      }

    private def save(label:Question.Label)(id:Question.Id)(implicit xa:Transactor[Task]): Task[Long] =
      run(quote {
        questionTable.insert(lift(Question(id, label)))
      }).transact(xa)


  }

}

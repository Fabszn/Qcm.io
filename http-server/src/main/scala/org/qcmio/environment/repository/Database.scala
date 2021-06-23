package org.qcmio.environment.repository

import doobie.Transactor
import org.qcmio.model
import doobie.implicits._
import doobie.quill.DoobieContext
import io.getquill._
import org.qcmio.model.Question
import zio.Task
import zio.interop.catz._

private[repository] final case class Database(xa: Transactor[Task])
    extends QuestionsRepository.Service {

  val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._
  def saveQuestion(question: model.Question): Task[Long] = run(quote {
    query[Question].insert(lift(question))
  }).transact(xa)
}

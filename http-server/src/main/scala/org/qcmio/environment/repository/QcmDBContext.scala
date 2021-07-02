package org.qcmio.environment.repository

import doobie.quill.DoobieContext
import io.getquill.{EntityQuery, SnakeCase}
import org.qcmio.model.Question
import zio.interop.catz._

private[repository] trait DBContext {
  val ctx = new QcmIODBContext
}

private[repository] class QcmIODBContext extends DoobieContext.Postgres(SnakeCase) {

  val nextId = quote(infix"""select nextval('seq_question')""".as[Question.Id])

  val questionTable = quote(querySchema[Question]("t_question", _.id -> "pkid_question"))

}

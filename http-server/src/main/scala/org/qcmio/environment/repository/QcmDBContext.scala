package org.qcmio.environment.repository

import doobie.quill.DoobieContext
import io.getquill.{EntityQuery, SnakeCase}
import org.qcmio.model.{Account, Candidat, Question, Reponse}
import zio.interop.catz._

private[repository] trait DBContext {
  val ctx = new QcmIODBContext
}

private[repository] class QcmIODBContext extends DoobieContext.Postgres(SnakeCase) {

  val nextQuestionId = quote(infix"""select nextval('seq_question')""".as[Question.Id])
  val nextReponseId = quote(infix"""select nextval('seq_reponse')""".as[Reponse.Id])
  val nextAccountId = quote(infix"""select nextval('seq_account')""".as[Account.Id])
  val nextCandidatId = quote(infix"""select nextval('seq_candidat')""".as[Candidat.Id])

  val questionTable = quote(querySchema[Question]("t_question", _.id -> "pkid_question"))

  val reponseTable = quote(querySchema[Reponse]("t_reponse", _.id -> "pkid_reponse"))

  val candidatTable = quote(querySchema[Candidat]("t_candidat", _.id -> "pkid_candidat"))

  val accountTable = quote(querySchema[Account]("t_account", _.id -> "pkid_account"))

}

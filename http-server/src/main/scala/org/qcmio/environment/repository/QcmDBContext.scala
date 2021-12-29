package org.qcmio.environment.repository

import doobie.quill.DoobieContext
import io.getquill.SnakeCase
import org.qcmio.model.kind.{Kind, from, to}
import org.qcmio.model.{Account, Examen, Question, Reponse, User, UserReponse}

private[repository] trait DBContext {
  val ctx = new QcmIODBContext
}

private[repository] class QcmIODBContext extends DoobieContext.Postgres(SnakeCase) {

  val nextQuestionId = quote(infix"""select nextval('seq_question')""".as[Question.Id])
  val nextReponseId  = quote(infix"""select nextval('seq_reponse')""".as[Reponse.Id])
  val nextAccountId  = quote(infix"""select nextval('seq_account')""".as[Account.Id])
  val nextUserId     = quote(infix"""select nextval('seq_candidat')""".as[User.Id])
  val nextExamenId     = quote(infix"""select nextval('seq_examen')""".as[Examen.Id])

  val questionTable = quote(querySchema[Question]("t_question", _.id -> "pkid_question"))

  val reponseTable = quote(querySchema[Reponse]("t_reponse", _.id -> "pkid_reponse", _.questionId -> "fkid_question"))

  val userTable = quote(querySchema[User]("t_user", _.id -> "pkid_user"))

  val accountTable = quote(querySchema[Account]("t_account", _.id -> "pkid_account", _.idUser -> "fkid_user"))

  val examenTable = quote(querySchema[Examen]("t_examen", _.id -> "pkid_examen", _.id -> "fkid_examen"))

  val reponseUserTable = quote(querySchema[UserReponse]("t_examen", _.id -> "pkid_examen", _.id -> "fkid_examen"))

  implicit val encodeUUID = MappedEncoding[Kind, String](from)
  implicit val decodeUUID = MappedEncoding[String, Kind](to)

}

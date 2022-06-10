package org.qcmio.environment

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.{Literal, PostgresZioJdbcContext}
import org.flywaydb.core.Flyway
import org.qcmio.environment.config.config
import org.qcmio.environment.config.config.getConf
import org.qcmio.environment.domain.model._
import org.qcmio.environment.domain.model.kind.Kind
import zio._

import javax.sql.DataSource

package object repository {

  object QuillContext extends PostgresZioJdbcContext(Literal) {
      val dataSourceLayer: RLayer[Has[config.Configuration], Has[DataSource]] =
        getConf.toManaged_.flatMap { conf =>
          ZManaged
            .make(ZIO.effect {
              new HikariDataSource(
                new HikariConfig {
                  setJdbcUrl(conf.db.url)
                  setUsername(conf.db.user)
                  setPassword(conf.db.password)
                  setDriverClassName(conf.db.driver)
                  setMaximumPoolSize(conf.db.maximumPoolSize)
                  setMinimumIdle(conf.db.minimumIdleSize)
                }
              )
            })(ds => ZIO.effect(ds.close()).ignore)
            .tap(
              ds =>
                Task {
                  Flyway
                    .configure()
                    .dataSource(ds)
                    .load()
                    .migrate()
                }.toManaged_
            )
            .orDie
        }.toLayer



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

    implicit val encodeUUID = MappedEncoding[Kind, String](kind.from)
    implicit val decodeUUID = MappedEncoding[String, Kind](kind.to)


    }

}

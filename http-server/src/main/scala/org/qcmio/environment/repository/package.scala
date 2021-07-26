package org.qcmio.environment

import cats.effect
import cats.effect.Blocker
import doobie.hikari.HikariTransactor
import org.qcmio.environment.config.Configuration
import org.qcmio.environment.config.Configuration.getDbConf
import org.qcmio.model.Question
import zio._
import zio.blocking.Blocking
import zio.interop.catz._

package object repository {

  type DbTransactor = Has[DbTransactor.Resource]
  type QuestionRepository = Has[QuestionsRepository.Service]

  object question {
    def saveQuestion(q: Question.Label): RIO[QuestionRepository with Blocking, Long] =
      RIO.accessM(_.get.saveQuestion(q))

    def getQuestion(id: Question.Id): RIO[QuestionRepository with Blocking, Option[Question]] =
      RIO.accessM(_.get.getQuestion(id))
  }

  object DbTransactor {

    val postgres: ZLayer[Configuration with Blocking, Nothing, DbTransactor] = {

      ZLayer.fromManaged(
        ZIO.runtime[Blocking].toManaged_.flatMap { implicit rt =>
          for {
            blockingEC <- Managed.succeed(
              rt.environment
                .get[Blocking.Service]
                .blockingExecutor
                .asEC
            )
            connectEC = rt.platform.executor.asEC
            conf <- getDbConf.toManaged_
            managed = new Resource {
              val xa: effect.Resource[Task, HikariTransactor[Task]] = HikariTransactor.newHikariTransactor[Task](
                conf.driver,
                conf.url,
                conf.user,
                conf.password,
                connectEC,
                Blocker.liftExecutionContext(blockingEC)
              )
            }
          } yield managed
        })
    }


    trait Resource {
      val xa: effect.Resource[Task, HikariTransactor[Task]]
    }
  }
}

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



  object DbTransactor {

    val postgres: ZLayer[Blocking with Configuration, Throwable, Has[Resource]] = {

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
            trans <- HikariTransactor.newHikariTransactor[Task](
                conf.driver,
                conf.url,
                conf.user,
                conf.password,
                connectEC,
                Blocker.liftExecutionContext(blockingEC)
              ).toManaged
          } yield new Resource {
            override val xa: HikariTransactor[Task] =trans
          }
        })
    }

    trait Resource {
      val xa: HikariTransactor[Task]
    }
  }
}

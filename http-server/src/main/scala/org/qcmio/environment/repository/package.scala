package org.qcmio.environment

import cats.effect.Blocker
import doobie.hikari.HikariTransactor
import org.qcmio.environment.config.Configuration
import org.qcmio.model.Question
import zio._
import zio.blocking.Blocking
import zio.interop.catz._

package object repository {

  type DbTransactor       = Has[DbTransactor.Resource]
  type QuestionRepository = Has[QuestionsRepository.Service]

  object question {
    def saveQuestion(q: Question.Label): RIO[QuestionRepository with Blocking, Long] =
      RIO.accessM(_.get.saveQuestion(q))

    def getQuestion(id: Question.Id): RIO[QuestionRepository with Blocking, Option[Question]] =
      RIO.accessM(_.get.getQuestion(id))
  }
  object DbTransactor {

    val postgres: URLayer[Has[Configuration.DbConf], DbTransactor] =
      ZLayer.fromService(
        conf =>
          new Resource {
              val xa: ZManaged[Blocking, Throwable, HikariTransactor[Task]] =
                ZIO.runtime[Blocking].toManaged_.flatMap { implicit rt =>
                  for {
                    blockingEC <- Managed.succeed(
                      rt.environment
                        .get[Blocking.Service]
                        .blockingExecutor
                        .asEC
                    )
                    connectEC = rt.platform.executor.asEC
                    managed <- HikariTransactor.newHikariTransactor[Task](
                      conf.driver,
                      conf.url,
                      conf.user,
                      conf.password,
                      connectEC,
                      Blocker.liftExecutionContext(blockingEC)
                    ).toManaged
                  } yield managed
                }
          }
      )

    trait Resource {
      val xa:ZManaged[Blocking, Throwable, HikariTransactor[Task]]
    }
  }
}

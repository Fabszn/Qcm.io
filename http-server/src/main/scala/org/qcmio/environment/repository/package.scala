package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import zio._
import doobie._
import org.qcmio.model.Question
import zio.interop.catz._

package object repository {

  type DbTransactor       = Has[DbTransactor.Resource]
  type QuestionRepository = Has[QuestionsRepository.Service]


  object question {
    def saveQuestion(q: Question.Label): RIO[QuestionRepository, Long] =
      RIO.accessM(_.get.saveQuestion(q))

    def getQuestion(id:Question.Id) : RIO[QuestionRepository,Option[Question]] =
      RIO.accessM(_.get.getQuestion(id))
  }
  object DbTransactor {

    val postgres: URLayer[Has[Configuration.DbConf], DbTransactor] =
      ZLayer.fromService(
        conf =>
          new Resource {
            override val xa: Transactor[Task] = Transactor.fromDriverManager(
              conf.driver,
              conf.url,
              conf.user,
              conf.password
            )
          }
      )

    trait Resource {
      val xa: Transactor[Task]
    }
  }

}

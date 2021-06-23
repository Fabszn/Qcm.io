package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import zio._
import doobie._
import zio.interop.catz._

package object repository {

  type DbTransactor = Has[DbTransactor.Resource]
  type QuestionRepository = Has[QuestionsRepository.Service]

  object DbTransactor {

    trait Resource {
      val xa: Transactor[Task]
    }
    val postgres: URLayer[Has[Configuration.DbConf], DbTransactor] =
      ZLayer.fromService(conf =>
        new Resource {
          override val xa: Transactor[Task] = Transactor.fromDriverManager(
            conf.driver,
            conf.url,
            conf.user,
            conf.password
          )
        }
      )
  }

}

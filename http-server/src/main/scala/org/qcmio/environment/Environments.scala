package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import org.qcmio.environment.repository.{DbTransactor, QuestionRepository, QuestionsRepository}
import zio.{ULayer, URLayer, ZLayer}
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type HttpEnvironment = Configuration with Clock

  type AppEnvironment = HttpEnvironment with QuestionRepository

  val dbTransactor: URLayer[Configuration with Blocking, DbTransactor] =
    DbTransactor.postgres
  val questionRepository: URLayer[Configuration with Blocking, QuestionRepository] =
    dbTransactor >>> QuestionsRepository.live
  val appEnvironment: ULayer[QuestionRepository] =
    (Configuration.live ++ Blocking.live) >>> questionRepository

}

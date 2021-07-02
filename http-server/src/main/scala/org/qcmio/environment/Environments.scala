package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import org.qcmio.environment.repository.{DbTransactor, QuestionRepository, QuestionsRepository}
import zio.ULayer
import zio.clock.Clock

object Environments {

  type HttpEnvironment = Configuration with Clock

  type AppEnvironment = HttpEnvironment with QuestionRepository

  val httpServerEnvironment: ULayer[HttpEnvironment] =
    Configuration.live ++ Clock.live
  val dbTransactor: ULayer[DbTransactor] =
    Configuration.live >>> DbTransactor.postgres
  val questionRepository: ULayer[QuestionRepository] =
    dbTransactor >>> QuestionsRepository.live
  val appEnvironment: ULayer[AppEnvironment] =
    httpServerEnvironment ++ questionRepository

}

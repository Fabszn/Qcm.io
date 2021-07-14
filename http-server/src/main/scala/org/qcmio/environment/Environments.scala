package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import org.qcmio.environment.repository.{DbTransactor, QuestionRepository, QuestionsRepository}
import zio.{ULayer, ZLayer}
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type HttpEnvironment = Configuration with Clock

  type AppEnvironment = HttpEnvironment with QuestionRepository with Blocking

  val httpServerEnvironment: ULayer[HttpEnvironment] =
    Configuration.live ++ Clock.live
  val dbTransactor: ULayer[DbTransactor] =
    Configuration.live >>> DbTransactor.postgres
  val questionRepository: ULayer[QuestionRepository with Blocking] =
    dbTransactor >>> QuestionsRepository.live ++ Blocking.live
  val appEnvironment: ULayer[AppEnvironment] =
    httpServerEnvironment ++ questionRepository

}

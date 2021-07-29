package org.qcmio.environment

import org.qcmio.environment.Environments.questionRepository
import org.qcmio.environment.config.Configuration
import org.qcmio.environment.repository.{DbTransactor, QuestionRepository, QuestionsRepository}
import zio.{ULayer, URLayer, ZLayer}
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type HttpServerEnvironment = Configuration with Clock
  type AppEnvironment = Configuration with Clock with QuestionRepository

  val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
  val dbTransactor: ULayer[DbTransactor] =
  Configuration.live ++ Blocking.live >>> DbTransactor.postgres
  val questionRepository: ULayer[QuestionRepository] =
    dbTransactor >>> QuestionsRepository.live
  val appEnvironment: ULayer[AppEnvironment] =
    questionRepository ++ httpServerEnvironment

}

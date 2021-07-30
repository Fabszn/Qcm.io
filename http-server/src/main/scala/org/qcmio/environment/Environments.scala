package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import org.qcmio.environment.repository.{DbTransactor, QuestionRepository, QuestionsRepository}
import zio.{Has, Layer, ULayer}
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type HttpServerEnvironment = Configuration with Clock
  type AppEnvironment = Configuration with Clock with QuestionRepository

  val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
  val dbTransactor: Layer[Throwable, Has[DbTransactor.Resource]] =
  Configuration.live ++ Blocking.live >>> DbTransactor.postgres
  val questionRepository: Layer[Throwable,QuestionRepository] =
    dbTransactor >>> QuestionsRepository.live
  val appEnvironment: Layer[Throwable,AppEnvironment] =
    questionRepository ++ httpServerEnvironment

}

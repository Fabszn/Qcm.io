package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import org.qcmio.environment.repository.{DbTransactor, QuestionRepository, QuestionsRepository}
import zio.{ULayer, URLayer, ZLayer}
import zio.blocking.Blocking
import zio.clock.Clock

object Environments {

  type HttpEnvironment = Configuration with Clock

  type AppEnvironment = HttpEnvironment with QuestionRepository with Blocking

  val httpServerEnvironment: ULayer[HttpEnvironment] =
    Configuration.live ++ Clock.live
  val dbTransactor: URLayer[Configuration with Blocking, DbTransactor] =
    DbTransactor.postgres
  val questionRepository: URLayer[Configuration with Blocking, QuestionRepository with Blocking] =
    dbTransactor >>> QuestionsRepository.live ++ Blocking.live
  val appEnvironment: URLayer[Configuration with Blocking, HttpEnvironment with QuestionRepository with Blocking] =
    httpServerEnvironment ++ questionRepository

}

package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import org.qcmio.environment.repository._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.{Has, Layer, ULayer}

object Environments {

  type HttpServerEnvironment = Configuration with Clock
  type AppEnvironment = Blocking with Configuration with Clock with QuestionRepository with AdministratorRepository

  val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
  val dbTransactor: Layer[Throwable, Has[DbTransactor.Resource]] =
  Configuration.live ++ Blocking.live >>> DbTransactor.postgres
  val questionRepository: Layer[Throwable,QuestionRepository] =
    dbTransactor >>> QuestionsRepository.live
  val userRepository: Layer[Throwable,UserRepository] =
    dbTransactor >>> UsersRepository.live
  val adminRepository: Layer[Throwable,AdministratorRepository] =
    dbTransactor >>> AdminRepository.live
  val appEnvironment: Layer[Throwable,AppEnvironment] =
    Blocking.live ++ adminRepository ++ userRepository ++ questionRepository ++ httpServerEnvironment

}

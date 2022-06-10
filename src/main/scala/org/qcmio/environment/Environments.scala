package org.qcmio.environment


import org.qcmio.environment.config.config
import org.qcmio.environment.config.config.Configuration
import org.qcmio.environment.repository._
import org.qcmio.environment.repository.adminRepository.AdminRepository
import org.qcmio.environment.repository.examenRepository.ExamenRepository
import org.qcmio.environment.repository.questionsRepository.QuestionRepository
import org.qcmio.environment.repository.userRepository.UserRepository
import zio.blocking.Blocking
import zio.clock.Clock
import zio._

import javax.sql.DataSource


object Environments {

  // dependance description

  type AppEnvironment = Blocking with Has[Configuration] with Clock
    with Has[QuestionRepository] with Has[AdminRepository] with Has[ExamenRepository] with Has[UserRepository]


  val configurationLayer: ULayer[Has[config.Configuration]] = config.layer
  val dbDataSourceLayer: TaskLayer[Has[DataSource]] = (configurationLayer ++ Blocking.live) >>> repository.QuillContext.dataSourceLayer
  val questionRepositoryLayer: TaskLayer[Has[QuestionRepository]] = dbDataSourceLayer >>> questionsRepository.layer
  val userRepositoryLayer: TaskLayer[Has[userRepository.UserRepository]] = dbDataSourceLayer >>> userRepository.layer
  val adminRepositoryLayer: TaskLayer[Has[AdminRepository]] = dbDataSourceLayer >>> adminRepository.layer
  val examenRepositoryLayer: TaskLayer[Has[ExamenRepository]] = dbDataSourceLayer >>> examenRepository.layer


  val appEnvironment: ZLayer[Any, Throwable, AppEnvironment] =
    Blocking.live ++
      Clock.live ++
      configurationLayer ++
      adminRepositoryLayer ++
      examenRepositoryLayer ++
      userRepositoryLayer ++
      questionRepositoryLayer

}

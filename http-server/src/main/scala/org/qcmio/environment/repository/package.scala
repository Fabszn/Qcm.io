package org.qcmio.environment

import cats.effect.Blocker
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import org.qcmio.environment.config.Configuration
import org.qcmio.environment.config.Configuration.getDbConf
import zio._
import zio.blocking.Blocking
import zio.interop.catz._

import javax.sql.DataSource

package object repository {

  type DbTransactor = Has[DbTransactor.Resource]
  type QuestionRepository = Has[QuestionsRepository.Service]
  type UserRepository = Has[UsersRepository.Service]
  type AdministratorRepository = Has[AdminRepository.Service]
  type ExamenRepository = Has[ExamenRepository.Service]



  object DbTransactor {
    val dataSourceLayer: RLayer[Has[Configuration], Has[DataSource]] =
    getConf.toManaged_.flatMap { conf =>
      ZManaged
        .make(ZIO.effect {
          new HikariDataSource(
            new HikariConfig {
              setJdbcUrl(conf.db.url)
              setUsername(conf.db.user)
              setPassword(conf.db.password)
              setDriverClassName(conf.db.driver)
              setMaximumPoolSize(conf.db.maximumPoolSize)
              setMinimumIdle(conf.db.minimumIdleSize)
            }
          )
        })(ds => ZIO.effect(ds.close()).ignore)
        .tap(
          ds =>
            Task {
              Flyway
                .configure()
                .dataSource(ds)
                .load()
                .migrate()
            }.toManaged_
        )
        .orDie



    val postgres: ZLayer[Configuration, Throwable, Has[Resource]] = {

      ZLayer.fromManaged(
        ZIO.runtime[Blocking].toManaged_.flatMap { implicit rt =>
          for {
            blockingEC <- Managed.succeed(
              rt.environment
                .get[Blocking.Service]
                .blockingExecutor
                .asEC
            )
            connectEC = rt.platform.executor.asEC
            conf <- getDbConf.toManaged_
            trans <- HikariTransactor.newHikariTransactor[Task](
                conf.driver,
                conf.url,
                conf.user,
                conf.password,
                connectEC,
                Blocker.liftExecutionContext(blockingEC)
              ).toManaged
          } yield new Resource {
            override val xa: HikariTransactor[Task] =trans
          }
        })
    }

    trait Resource {
      val xa: HikariTransactor[Task]
    }
  }
}

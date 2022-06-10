package org.qcmio.environment.config

import pureconfig.ConfigSource
import zio.{Has, RIO, Task, ULayer, URIO, ZIO, ZLayer}
import pureconfig.generic.auto._

object config {

  final case class HttpConf(port: Int, host: String)
  final case class DbConf(url: String, driver: String, user: String, password: String, maximumPoolSize: Int, minimumIdleSize: Int)
  final case class JwtConf(secretKey:String, algo: String)
  final case class GlobalConfig(db: DbConf, httpServer: HttpConf, jwt:JwtConf)



  trait Configuration {
    def getConf:Task[GlobalConfig]
  }

  private final case class ConfigurationService() extends Configuration {
    override def getConf: Task[GlobalConfig] = ZIO
      .effect(ConfigSource.default.loadOrThrow[GlobalConfig]).orDie
  }

  val layer: ULayer[Has[Configuration]] = ZLayer.succeed(ConfigurationService())

  def getConf: RIO[Has[Configuration], GlobalConfig] = ZIO.serviceWith[Configuration](_.getConf)

}

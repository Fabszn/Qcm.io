package org.qcmio.environment.config

import pureconfig.ConfigSource
import zio.{Has, ULayer, URIO, ZIO, ZLayer}
import pureconfig.generic.auto._

object Configuration {

  final case class HttpConf(port: Int, host: String)

  final case class DbConf(
      url: String,
      driver: String,
      user: String,
      password: String
  )

  final case class JwtConf(secretKey:String, algo: String)

  final case class AppConfig(database: DbConf, httpServer: HttpConf, jwt:JwtConf)

  val live: ULayer[Configuration] = ZLayer.fromEffectMany(
    ZIO
      .effect(ConfigSource.default.loadOrThrow[AppConfig])
      .map(c => Has(c.httpServer) ++ Has(c.database) ++ Has(c.jwt))
      .orDie
  )

  def getDbConf:URIO[Configuration,DbConf] = ZIO.access(_.get[DbConf])

  def getJwtConf:URIO[Configuration,JwtConf] = ZIO.access(_.get[JwtConf])

}

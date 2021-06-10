package org.qcmio.environment.config

import pureconfig.ConfigSource
import zio.{Has, ULayer, ZIO, ZLayer}
import pureconfig.generic.auto._

object Configuration {

  final case class HttpConf(port: Int, host: String)

  final case class DbConf(url: String, driver: String, user: String, password: String)

  final case class AppConfig(database: DbConf, httpServer: HttpConf)


    val live: ULayer[Configuration] = ZLayer.fromEffectMany(
      ZIO.effect(ConfigSource.default.loadOrThrow[AppConfig]).map(c => Has(c.httpServer) ++ Has(c.database)).orDie
    )

}

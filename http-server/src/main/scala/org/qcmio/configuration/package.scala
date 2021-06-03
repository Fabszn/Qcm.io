package org.qcmio

import org.qcmio.model.QCMErrorReport
import org.qcmio.model.settings.{Api, Db}
import pureconfig.ConfigSource
import zio.{Has, IO, ULayer, ZIO, ZLayer}
import pureconfig.generic.auto._

package object configuration {

  type Configuration = Has[Configuration.Service]

  object Configuration {
    trait Service {
      def loadApi: IO[QCMErrorReport, Api]
      def loadBb: IO[QCMErrorReport, Db]
    }

    val live: ULayer[Configuration] = ZLayer.succeed {
      new Service {
        override def loadBb: IO[QCMErrorReport, Db] = ZIO
          .fromEither(ConfigSource.default.at("db").load[Db])
          .mapError(cf => QCMErrorReport(cf.prettyPrint()))

        override def loadApi: IO[QCMErrorReport, Api] = ZIO
          .fromEither(ConfigSource.default.at("http").load[Api])
          .mapError(cf => QCMErrorReport(cf.prettyPrint()))
      }
    }

  }
  def loadApi: ZIO[Configuration, QCMErrorReport, Api] =
    ZIO.accessM(_.get.loadApi)

}

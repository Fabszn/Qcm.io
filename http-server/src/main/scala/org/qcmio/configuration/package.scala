package org.qcmio

import org.qcmio.model.QCMErrorReport
import org.qcmio.model.settings.Api
import pureconfig.ConfigSource
import zio.{Has, IO, ULayer, ZIO, ZLayer}
import pureconfig.generic.auto._

package object configuration {

  type Configuration = Has[Configuration.Service]

  object Configuration {
    trait Service {
      def loadApi: IO[QCMErrorReport, Api]
    }

    val live: ULayer[Configuration] = ZLayer.succeed {
      new Service {
        override def loadApi: IO[QCMErrorReport, Api] = ZIO
          .fromEither(ConfigSource.default.at("api").load[Api])
          .mapError(cf => QCMErrorReport(cf.prettyPrint()))
      }
    }

  }
  def loadApi: ZIO[Configuration, QCMErrorReport, Api] =
    ZIO.accessM(_.get.loadApi)

}

package org.qcmio

import org.qcmio.model.QCMErrorReport
import zio.{Has, IO, ULayer, ZLayer}

package object database {

  type Database = Has[Database.Service]
  object Database {
    trait Service {
      def selectById(id: Long): IO[QCMErrorReport, String]
    }
  }

  val live: ULayer[Database] = ZLayer.succeed {
    def selectById(id: Long): IO[QCMErrorReport, String] = ???

  }
}

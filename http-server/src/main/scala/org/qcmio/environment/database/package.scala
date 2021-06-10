package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import org.qcmio.model.QCMErrorReport
import zio.{Has, IO, ULayer, URLayer, ZLayer}

package object database {

 /* type Database = Has[Database.Service]
  object Database {
    trait Service {
      def selectById(id: Long): IO[QCMErrorReport, String]
    }
  }

  val live: URLayer[Has[Configuration], Database] = ZLayer.fromService{ cfg: Configuration =>

    def selectById(id: Long): IO[QCMErrorReport, String] = {


      ???
    }

  }*/
}

package org.qcmio.environment

import org.qcmio.environment.config.Configuration.{DbConf, HttpConf}
import zio.{Has, UIO, URIO, ZIO}

package object config {
  type Configuration = Has[HttpConf] with Has[DbConf]



}

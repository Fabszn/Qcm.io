package org.qcmio.environment

import org.qcmio.environment.config.Configuration.{DbConf, HttpConf, JwtConf}
import zio.{Has}

package object config {
  type Configuration = Has[HttpConf] with Has[DbConf] with Has[JwtConf]



}

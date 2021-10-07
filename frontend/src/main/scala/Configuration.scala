package org.qcmio.front

import pureconfig.ConfigSource
import pureconfig.generic.auto._


object Configuration {

  case class HttpBackend(port: Int, host: String)


  final case class FrontEndConfig(httpServer: HttpBackend)



  val frontConf = ConfigSource.default.loadOrThrow[FrontEndConfig]

  val backendUrl = s"http://${frontConf.httpServer.host}:${frontConf.httpServer.port}"

}

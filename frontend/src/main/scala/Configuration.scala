package org.qcmio.front




object Configuration {


  case class HttpBackend(port: Int, host: String)


  final case class FrontEndConfig(httpServer: HttpBackend)




  //val frontConf = ConfigSource.default.loadOrThrow[FrontEndConfig]

  //val backendUrl = s"http://${conf.getString("http-backend.host")}:${conf.getString("http-backend.port")}"
  val backendUrl = s"http://localhost:8088"

}

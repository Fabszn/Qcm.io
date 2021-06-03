package org.qcmio

object model {

  final case class QCMErrorReport(value: String) extends AnyVal

  object settings {

    final case class Api(port: Int, host: String)
    final case class Db(url:String, driver:String, user:String,password:String)

  }

}

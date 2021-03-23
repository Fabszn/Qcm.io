package org.qcmio

object model {

  final case class QCMErrorReport(value: String) extends AnyVal

  object settings {

    final case class Api(port: Int, host: String)

  }

}

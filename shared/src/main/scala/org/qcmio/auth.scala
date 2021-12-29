package org.qcmio

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

object auth {

  sealed trait Role

  case object Administrateur extends Role
  case object Student        extends Role

  case class AuthenticatedUser(login: String, role: Role = Student)

  final case class LoginInfo(login: String, mdp: String)

  object LoginInfo {
    implicit val labelDecoder: Decoder[LoginInfo] = deriveDecoder[LoginInfo]
    implicit val labelEncoder: Encoder[LoginInfo] = deriveEncoder[LoginInfo]
  }

}

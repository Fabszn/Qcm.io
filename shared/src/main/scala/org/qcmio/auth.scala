package org.qcmio

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}


object auth {

  trait User
  case class AuthenticatedUser(login:String) extends User
  case object NoAuthorizedUser extends User

  final case class LoginInfo(login:String, mdp:String)


  object LoginInfo{
    implicit  val labelDecoder: Decoder[LoginInfo] = deriveDecoder[LoginInfo]
    implicit  val labelEncoder: Encoder[LoginInfo] = deriveEncoder[LoginInfo]
  }

}

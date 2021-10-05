package org.qcmio

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}


object auth {

  case class User(login:String, mdp:String)

  object User{
    implicit  val labelDecoder: Decoder[User] = deriveDecoder[User]
    implicit  val labelEncoder: Encoder[User] = deriveEncoder[User]
  }

}

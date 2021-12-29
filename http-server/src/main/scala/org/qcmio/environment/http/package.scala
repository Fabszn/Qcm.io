package org.qcmio.environment

import cats._
import cats.data._
import io.circe.Encoder
import org.http4s._
import org.http4s.circe.jsonEncoderOf
import org.http4s.util.CaseInsensitiveString
import org.qcmio.Keys
import org.qcmio.auth.AuthenticatedUser
import org.qcmio.environment.Environments.AppEnvironment
import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.environment.http.jwt.JwtUtils
import zio.RIO


package object http {


  type ServerRIO[A] = RIO[AppEnvironment, A]
  type OT[A] = OptionT[ServerRIO, A]
  type QCMTask[A] = RIO[AppEnvironment, A]

  implicit def jsonEncoder[F[_] : Applicative, A](
                                                   implicit
                                                   encoder: Encoder[A]
                                                 ): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  def authUser(conf: JwtConf): Kleisli[OT, Request[ServerRIO], AuthenticatedUser] =
    Kleisli(
      r =>
        OptionT(
          RIO {
            r.headers
              .get(CaseInsensitiveString(Keys.tokenHeader))
              .fold(Option.empty[AuthenticatedUser])(
               token =>
                if (JwtUtils.isValidToken(token.value, conf)) {
                  //todo Decode Token to get user Info
                  Some(AuthenticatedUser("To be completed"))
                } else {
                  None
                }

          )}
          )
    )


}

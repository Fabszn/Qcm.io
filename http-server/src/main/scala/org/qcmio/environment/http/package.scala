package org.qcmio.environment

import cats.Applicative
import cats.data.{Kleisli, OptionT}
import io.circe.Encoder
import org.http4s.CharsetRange.*
import org.http4s.circe.jsonEncoderOf
import org.http4s.util.CaseInsensitiveString
import org.http4s.{EntityEncoder, Request}
import org.qcmio.Keys
import org.qcmio.auth.{AuthenticatedUser, NoAuthorizedUser, User}
import org.qcmio.environment.Environments.AppEnvironment
import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.environment.http.jwt.JwtUtils
import zio.{RIO, Task}
import zio.interop.catz._

package object http {

  implicit def jsonEncoder[F[_]: Applicative, A](
      implicit
      encoder: Encoder[A]
  ): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  type ServerRIO[A] = RIO[AppEnvironment, A]


  //def authUser(conf: JwtConf): Kleisli[OptionT[Task,_<:User], Request[Task], _ <: User] =
  def authUser(conf: JwtConf): Kleisli[OptionT[Task, *], Request[Task], User] =
  Kleisli(
      r =>
        OptionT.liftF(Task[User](
          r.headers
            .get(CaseInsensitiveString(Keys.tokenHeader))
            .map { token =>
              if (JwtUtils.isValidToken(token.value, conf)) {
                //todo Decode Token to get user Info
                AuthenticatedUser("To be completed")
              } else {
                NoAuthorizedUser
              }
            }
            .getOrElse(NoAuthorizedUser)
        ))
    )

}

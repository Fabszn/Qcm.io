package org.qcmio.environment

import cats.Applicative
import cats.data.Kleisli
import io.circe.Encoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.util.CaseInsensitiveString
import org.http4s.{EntityEncoder, Request}
import org.qcmio.Keys
import org.qcmio.auth.{AuthenticatedUser, NoAuthorizedUser, User}
import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.environment.http.jwt.JwtUtils
import zio.Task

package object http {

  implicit def jsonEncoder[F[_]: Applicative, A](
      implicit
      encoder: Encoder[A]
  ): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  def authUser(conf: JwtConf): Kleisli[Task, Request[Task], User] =
    Kleisli(
      r =>
        Task[User](
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
        )
    )

}

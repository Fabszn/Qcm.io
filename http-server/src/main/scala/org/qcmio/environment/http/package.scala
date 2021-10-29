package org.qcmio.environment

import cats._, cats.effect._, cats.implicits._, cats.data._
import io.circe.Encoder

import org.http4s.circe.jsonEncoderOf
import org.http4s.util.CaseInsensitiveString

import org.qcmio.Keys
import org.qcmio.auth.{AuthenticatedUser, NoAuthorizedUser, User}
import org.qcmio.environment.Environments.AppEnvironment
import org.qcmio.environment.config.Configuration.JwtConf
import org.qcmio.environment.http.jwt.JwtUtils
import zio.{RIO, Task}
import zio.interop.catz._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server._
import zio._


package object http {

  implicit def jsonEncoder[F[_]: Applicative, A](
      implicit
      encoder: Encoder[A]
  ): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  type ServerRIO[A] = RIO[AppEnvironment, A]


  //def authUser(conf: JwtConf): Kleisli[Task, Request[Task], _ <: User] =
  def authUser(conf: JwtConf): Kleisli[OptionT[ServerRIO, *], Request[ServerRIO], User] =
  Kleisli(
      r =>
        OptionT(RIO{
          r.headers
            .get(CaseInsensitiveString(Keys.tokenHeader))
            .map { token =>
              val t: User = if (JwtUtils.isValidToken(token.value, conf)) {
                //todo Decode Token to get user Info
                AuthenticatedUser("To be completed")
              } else {
                NoAuthorizedUser("")
              }
              t
            }.getOrElse(NoAuthorizedUser(""))
        })
    )




def authUser2: Kleisli[OptionT[Task,*], Request[Task], AuthenticatedUser] =
  Kleisli(
  r =>{
  //val f: OptionT[Task, AuthenticatedUser] =OptionT[Task, AuthenticatedUser] {

    OptionT.liftF(Task(AuthenticatedUser("To be completed")))


  }
  )

}

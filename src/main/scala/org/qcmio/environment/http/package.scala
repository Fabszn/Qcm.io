package org.qcmio.environment

import cats._
import cats.data._
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.parser._

import org.http4s._
import org.http4s.circe.jsonEncoderOf
import org.http4s.headers.Authorization
import org.qcmio.environment.Environments.AppEnvironment
import org.qcmio.environment.config.config.GlobalConfig
import pdi.jwt.{Jwt, JwtAlgorithm}
import zio.{RIO, Task}

import zio.interop.catz._
import scala.util.{Failure, Success}


package object http {

  type ApiTask[A] = RIO[AppEnvironment, A]

  type OT[A] = OptionT[ApiTask, A]


  implicit def jsonEncoder[F[_] : Applicative, A](
                                                   implicit
                                                   encoder: Encoder[A]
                                                 ): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  case class UserInfo(userId: String, firstname: String, isAdmin: Boolean)

  object UserInfo {

    import org.http4s.circe.jsonOf
    import io.circe.generic.auto._
    implicit val format = jsonOf[Task, UserInfo]

    def empty: UserInfo = UserInfo("-","-",isAdmin = false)
  }

  def authUser(conf: GlobalConfig): Kleisli[OT, Request[ApiTask], UserInfo] =
    Kleisli(
      request =>
        OptionT(
          Task(
            request.headers
              .get(Authorization.name)
              .map(
                token => {
                  Jwt.decode(
                    token.head.value,
                    conf.jwt.secretKey,
                    Seq(JwtAlgorithm.HS256)
                  )
                }
              )
              .fold {
                //logger.error(s"None token has been found in header")
                Option.empty[UserInfo]
              } {
                case Failure(e) => {
                 // logger.error(s"Authentification error : ${e.getMessage} ${e.getCause}")
                  Option.empty[UserInfo]
                }
                case Success(jw) => {
                  decode[UserInfo](jw.content).fold(
                    _ => Option.empty[UserInfo],
                    (user: UserInfo) => Option(user)
                  )
                }

              }
          )
        )
    )


}

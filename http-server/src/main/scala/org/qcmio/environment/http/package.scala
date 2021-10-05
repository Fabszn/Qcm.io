package org.qcmio.environment

import cats.Applicative
import cats.data.{Kleisli, OptionT}
import io.circe.Encoder
import org.http4s.{AuthedRoutes, EntityEncoder, Request}
import org.http4s.circe.jsonEncoderOf
import org.qcmio.auth.User
import zio.interop.catz.monadErrorInstance
import zio.{IO, Task}
import cats._
import cats.effect._
import cats.implicits._
import cats.data._

import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server._

package object http {

  implicit def jsonEncoder[F[_]: Applicative, A](
      implicit
      encoder: Encoder[A]
  ): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]


  //val authedRoutes: AuthedRoutes[User, Task] = ???

  val authUser: Kleisli[Task, Request[Task], User] = Kleisli((r:Request[Task]) => Task(User("","")))

}

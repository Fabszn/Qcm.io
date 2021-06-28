package org.qcmio.environment

import cats.Applicative
import io.circe.Encoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

package object http {

  implicit def jsonEncoder[F[_]: Applicative, A](implicit encoder: Encoder[A]): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

}

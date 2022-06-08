package org.qcmio.environment.domain

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.qcmio.environment.domain.model.Account.{LastConnexionDate, Password}
import org.qcmio.environment.domain.model.Reponse.IsCorrect
import org.qcmio.environment.domain.model.UserReponse.UserRep
import org.qcmio.environment.domain.model.kind.{Kind, Single}

import java.time.{LocalDate, ZonedDateTime}

object model {

  type QR = (Question, Seq[Reponse])

  final case class Examen(id: Examen.Id = Examen.Id(-1), date: LocalDate, label: Examen.Label)

  object Examen {
    final case class Id(value: Long) extends AnyVal

    final case class Label(value: String) extends AnyVal
  }

  final case class Question(
                             id: Question.Id = Question.Id(-1),
                             label: Question.Label,
                             kind: Kind = Single
                           )


  object Question {
    final case class Id(value: Long) extends AnyVal

    object Id {
      implicit val idDecoder: Decoder[Question.Id] = deriveDecoder[Question.Id]
      implicit val idEncoder: Encoder[Question.Id] = deriveEncoder[Question.Id]
    }

    final case class Label(value: String) extends AnyVal

    object Label {
      implicit val labelDecoder: Decoder[Label] = deriveDecoder[Label]
    }


  }

  object kind {
    sealed trait Kind

    case object Single extends Kind

    case object Multiple extends Kind

    case object Text extends Kind

    def from = (k: Kind) => k match {
      case Single => "Single"
      case Multiple => "Multiple"
      case Text => "Multiple"
    }

    def to = (s: String) => s match {
      case "Single" => Single
      case "Multiple" => Multiple
      case "Text" => Text
    }

    implicit val encodeFoo: Encoder[Kind] = new Encoder[Kind] {
      final def apply(a: Kind): Json = {
        Json.obj(
          ("kind", Json.fromString(from(a)))
        )
      }
    }

    implicit val decodeFoo: Decoder[Kind] = new Decoder[Kind] {
      final def apply(c: HCursor): Decoder.Result[Kind] =
        for {
          jkind <- c.downField("kind").as[String]
        } yield {
          to(jkind)
        }
    }


  }

  final case class Reponse(
                            id: Reponse.Id = Reponse.Id(-1),
                            label: Reponse.Label,
                            questionId: Question.Id,
                            isCorrect: IsCorrect
                          )

  object Reponse {
    final case class Id(value: Long) extends AnyVal

    object Id {
      implicit val idDecoder: Decoder[Reponse.Id] = deriveDecoder[Reponse.Id]
      implicit val idEncoder: Encoder[Reponse.Id] = deriveEncoder[Reponse.Id]
    }

    final case class Label(value: String) extends AnyVal

    object Label {
      implicit val labelDecoder: Decoder[Reponse.Label] = deriveDecoder[Reponse.Label]
      implicit val labelEncoder: Encoder[Reponse.Label] = deriveEncoder[Reponse.Label]
    }

    final case class IsCorrect(value: Boolean) extends AnyVal

    object IsCorrect {
      implicit val isCorrectDecoder: Decoder[Reponse.IsCorrect] = deriveDecoder[Reponse.IsCorrect]
      implicit val isCorrectEncoder: Encoder[Reponse.IsCorrect] = deriveEncoder[Reponse.IsCorrect]
    }

    implicit val reponseDecoder: Decoder[Reponse] = deriveDecoder[Reponse]

  }


  final case class UserReponse(
                                id: UserReponse.Id = UserReponse.Id(-1),
                                idQuestion: Question.Id,
                                idExamen: Examen.Id,
                                kind: Kind,
                                reponses: Seq[UserRep],
                                dateTime: ZonedDateTime = ZonedDateTime.now
                              )

  object UserReponse {
    final case class Id(value: Long) extends AnyVal

    final case class UserRep(value: String) extends AnyVal
  }

  final case class User(
                         id: User.Id,
                         prenom: User.Prenom,
                         nom: User.Nom,
                         email: User.Email
                       )

  object User {
    final case class Id(value: Long) extends AnyVal

    final case class Nom(value: String) extends AnyVal

    final case class Prenom(value: String) extends AnyVal

    final case class Email(value: String) extends AnyVal

    object Email {
      implicit val labelDecoder: Decoder[Email] = deriveDecoder[Email]
      implicit val labelEncoder: Encoder[Email] = deriveEncoder[Email]
    }

    final case class Mdp(value: String) extends AnyVal
  }

  final case class Account(id: Account.Id, idUser: User.Id, lastConnexion: LastConnexionDate, mdp: Password)

  object Account {
    final case class Id(value: Long) extends AnyVal

    final case class LastConnexionDate(value: ZonedDateTime) extends AnyVal

    final case class Password(value: String) extends AnyVal

  }

  final case class HttpReponse(
                                id: Option[model.Reponse.Id],
                                idQuestion: model.Question.Id,
                                label: model.Reponse.Label,
                                isCorrect: IsCorrect
                              )

  object HttpReponse {
    implicit val hrDecoder: Decoder[HttpReponse] = deriveDecoder[HttpReponse]
    implicit val hrEncoder: Encoder[HttpReponse] = deriveEncoder[HttpReponse]
  }

  final case class HttpSimpleReponse(
                                      id: Option[model.Reponse.Id],
                                      idQuestion: model.Question.Id,
                                      label: model.Reponse.Label
                                    )

  object HttpSimpleReponse {
    implicit val hrDecoder: Decoder[HttpSimpleReponse] = deriveDecoder[HttpSimpleReponse]
    implicit val hrEncoder: Encoder[HttpSimpleReponse] = deriveEncoder[HttpSimpleReponse]
  }

  final case class HttpQuestion(
                                 id: Option[Question.Id] = None,
                                 label: Question.Label,
                                 kind: Kind,
                                 reponses: Seq[HttpSimpleReponse] = Seq.empty[HttpSimpleReponse]
                               )

  object HttpQuestion {
    implicit val hqDecoder: Decoder[HttpQuestion] = deriveDecoder[HttpQuestion]
  }

}

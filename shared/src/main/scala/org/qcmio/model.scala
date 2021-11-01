package org.qcmio

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import org.qcmio.model.Account.LastConnexionDate
import org.qcmio.model.Reponse.IsCorrect

import java.time.ZonedDateTime

object model {


  final case class Question(
                             id: Question.Id = Question.Id(-1),
                             label: Question.Label,
                             reponses: List[Reponse] = List.empty[Reponse]
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

  final case class Candidat(
                             id: Candidat.Id,
                             prenom: Candidat.Prenom,
                             nom: Candidat.Nom,
                             email: Candidat.Email
                           )

  object Candidat {
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

  final case class Account(id: Account.Id, idCandidat: Candidat.Id, lastConnexion: LastConnexionDate)

  object Account {
    final case class Id(value: Long) extends AnyVal

    final case class LastConnexionDate(value: ZonedDateTime) extends AnyVal

  }

  final case class HttpReponse(
                                id: Option[Reponse.Id],
                                idQuestion: Question.Id,
                                label: Reponse.Label,
                                isCorrect: IsCorrect
                              )

  object HttpReponse {
    implicit val hrDecoder: Decoder[model.HttpReponse] = deriveDecoder[model.HttpReponse]
    implicit val hrEncoder: Encoder[model.HttpReponse] = deriveEncoder[model.HttpReponse]
  }

}

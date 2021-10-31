package org.qcmio

import org.qcmio.model.Reponse.isCorrect
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.qcmio.model.Account.LastConnexionDate
import org.qcmio.model.Question.Label

import java.time.ZonedDateTime

object model {

  final case class Question(
      id: Question.Id = Question.Id(-1),
      label: Question.Label
  )

  final case class Reponse(
      id: Reponse.Id,
      label: Reponse.Label,
      questionId: Question.Id,
      isCorrect: isCorrect


  )

  object Question {
    final case class Id(value: Long)      extends AnyVal
    object Id{
      implicit  val idDecoder: Decoder[Question.Id] = deriveDecoder[Question.Id]
    }
    final case class Label(value: String) extends AnyVal
    object Label{
      implicit  val labelDecoder: Decoder[Label] = deriveDecoder[Label]
    }
  }

  object Reponse {
    final case class Id(value: Long)         extends AnyVal
    final case class Label(value: String)    extends AnyVal
    final case class isCorrect(value: Boolean) extends AnyVal
  }

  final case class Candidat(
      id: Candidat.Id,
      prenom: Candidat.Prenom,
      nom: Candidat.Nom,
      email: Candidat.Email
  )

  object Candidat {
    final case class Id(value: Long)       extends AnyVal
    final case class Nom(value: String)    extends AnyVal
    final case class Prenom(value: String) extends AnyVal
    final case class Email(value: String)  extends AnyVal

    object Email{
      implicit  val labelDecoder: Decoder[Email] = deriveDecoder[Email]
      implicit  val labelEncoder: Encoder[Email] = deriveEncoder[Email]
    }
    final case class Mdp(value: String)  extends AnyVal
  }

  final case class Account(id:Account.Id, idCandidat:Candidat.Id, lastConnexion:LastConnexionDate)
  object Account {
    final case class Id(value: Long)       extends AnyVal
    final case class LastConnexionDate(value: ZonedDateTime) extends AnyVal

  }

}

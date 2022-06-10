package org.qcmio.environment.http

import cats.data.OptionT
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.qcmio.environment.domain.auth.AuthenticatedUser
import org.qcmio.environment.domain.model
import org.qcmio.environment.domain.model.{HttpReponse, Question, Reponse}
import org.qcmio.environment.repository.questionsRepository._
import org.qcmio.environment.utils.Mapper.{mapperAll, mapperOne}
import zio.interop.catz._

import scala.util.Try


object questionApi {

  val dsl = Http4sDsl[ApiTask]

  import dsl._

  object QuestionIdVar {
    def unapply(s: String): Option[Question.Id] = {
      if (s.nonEmpty)
        Try(Question.Id(s.toLong)).toOption
      else
        None
    }
  }

  object ReponseIdVar {
    def unapply(s: String): Option[Reponse.Id] = {
      if (s.nonEmpty)
        Try(Reponse.Id(s.toLong)).toOption
      else
        None
    }
  }

  val  api = AuthedRoutes.of[UserInfo, ApiTask] {
    case GET -> Root / QuestionIdVar(id) as user =>
      (for {
        q <- OptionT(question.getQuestion(id))
        reponses <- OptionT.liftF(question.getReponsesByQuestionId(id))
      } yield (q, reponses)).value >>= {
        case Some((q, rs)) => Ok(mapperOne(q, rs))
        case None => NotFound()
      }
    case GET -> Root as _ =>
      (for {
        qs <- question.getAllQuestionsReponses
      } yield mapperAll(qs)) >>= { l => Ok(l) }
    case authReq@POST -> Root / "reponse" as user =>
      for {
        rep <- authReq.req.as[HttpReponse]
          .map(hr => model.Reponse(label = hr.label, questionId = hr.idQuestion, isCorrect = hr.isCorrect))
        res <- reponse.saveReponse(rep)
        json <- Created(res.asJson)
      } yield json
    case authReq@POST -> Root as user =>
      for {
        lbl <- authReq.req.as[Question.Label]
        res <- question.saveQuestion(lbl)
        json <- Created(res.asJson)
      } yield json
    case authReq@PUT -> (Root / QuestionIdVar(id)) as user =>
      for {
        lbl <- authReq.req.as[Question.Label]
        res <- question.updateQuestion(id, lbl)
        json <- Created(res.asJson)
      } yield json
    case POST -> (Root / QuestionIdVar(idq) / "reponse" / ReponseIdVar(idr)) as user =>
      for {

        d <- Created("")
      } yield {
        d
      }

  }


}



package org.qcmio.environment.http

import cats.data.OptionT
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpRoutes}
import org.qcmio.auth.AuthenticatedUser
import org.qcmio.environment.repository.QuestionRepository
import org.qcmio.environment.repository.QuestionsRepository.{question, reponse}
import org.qcmio.environment.utils.Mapper.{mapperAll, mapperOne}
import org.qcmio.model
import org.qcmio.model.{HttpReponse, Question, Reponse}
import zio.RIO
import zio.interop.catz._

import scala.util.Try


final class QuestionsEndpoint {

  val dsl = Http4sDsl[QCMTask]

  import dsl._

  private val prefixPath = "/questions"

  object QuestionIdVar {
    def unapply(s: String): Option[Question.Id] = {
      if (s.nonEmpty)
        Try(Question.Id(s.toLong)).toOption
      else
        None
    }
  }

  object ReponseIdVar {
    def unapply(s:String):Option[Reponse.Id] = {
      if (s.nonEmpty)
        Try(Reponse.Id(s.toLong)).toOption
      else
        None
    }
  }

  private val httpRoutes = AuthedRoutes.of[AuthenticatedUser, QCMTask] {
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
    case POST -> (Root / QuestionIdVar(idq) / "reponse" / ReponseIdVar(idr) ) as user =>
      for{

        d <- Created("")
      }yield{
        d
      }

  }

  def routes(middleware: AuthMiddleware[QCMTask, AuthenticatedUser]): HttpRoutes[QCMTask] = Router(
    prefixPath -> middleware(httpRoutes)
  )


}



package org.qcmio.environment.utils

import org.qcmio.model.{HttpQuestion, HttpReponse, QR, Question, Reponse}

object Mapper {

  import io.scalaland.chimney.dsl._


  def mapperOne(question:Question, reponses:Seq[Reponse]):HttpQuestion ={

    val hq: HttpQuestion = question.into[HttpQuestion].transform
    val hrs = reponses.map(_.into[HttpReponse].withFieldRenamed(_.questionId,_.idQuestion).transform)

    hq.copy(reponses = hrs )
  }

  def mapperAll(qr :Seq[QR]):Seq[HttpQuestion] ={
   qr.map{
     case (question, reponses) => mapperOne(question , reponses)}
  }
}

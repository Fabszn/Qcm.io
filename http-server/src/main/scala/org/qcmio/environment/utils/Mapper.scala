package org.qcmio.environment.utils

import org.qcmio.model.{HttpQuestion, HttpReponse, Question, Reponse}

object Mapper {

  import io.scalaland.chimney.dsl._
  def mapper(question:Question, reponses:Seq[Reponse]):HttpQuestion ={

    val hq: HttpQuestion = question.into[HttpQuestion].transform
    val hrs = reponses.map(_.into[HttpReponse].withFieldRenamed(_.questionId,_.idQuestion).transform)

    hq.copy(reponses = hrs )
  }



}

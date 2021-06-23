package org.qcmio.environment.repository

import org.qcmio.model.Question
import zio.{Task, URLayer, ZLayer}

object QuestionsRepository {

  trait Service {
    def saveQuestion(question: Question): Task[Long]
  }

  val live: URLayer[DbTransactor, QuestionRepository] = ZLayer.fromService {
    resource: DbTransactor.Resource =>
      Database(resource.xa)
  }

}

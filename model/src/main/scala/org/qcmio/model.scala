package org.qcmio

object model {

  final case class Question(id: Question.Id, label: Question.Label)

  object Question {
    final case class Id(value: Long) extends AnyVal
    final case class Label(value: String) extends AnyVal
  }

}

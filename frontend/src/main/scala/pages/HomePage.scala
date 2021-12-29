package pages

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.circe.parser._
import org.qcmio.Keys
import org.qcmio.front.{Configuration, QcmIoCss, WithGlobalState}
import org.qcmio.model.{HttpQuestion, HttpSimpleReponse, Question, Reponse}
import org.scalajs.dom
import org.scalajs.dom.html

object HomePage extends WithGlobalState {

  case class QcmState(token: Option[String] = None) {
    def getToken: String = token.getOrElse("None token available")
  }

  val questionList = Var(Seq.empty[HttpQuestion])

  def homePage(gstate: QCMGlobalState) =
    div(
     div( a(href:="http://localhost:8088/examen", "hell")),
      div(
        loadQuestions(gstate),
        cls := QcmIoCss.questions.className.value,
        children <-- questionList.signal.map {
          case Nil => List(div("None question available"))
          case qs  => qs.map(h => displayQuestion(h))
        }
      )
    )

  def displayQuestion(httpQuestion: HttpQuestion): Div = {
    div(
      httpQuestion.label.value,
      displaySingleReponses(httpQuestion.id.getOrElse(Question.Id(-100)), httpQuestion.reponses)
    )
  }

  //def displayReponseByKind(question: HttpQuestion): Div = {???}

  def saveReponseByQuestion(hr: HttpSimpleReponse) =
    AjaxEventStream
      .post(
        s"${Configuration.backendUrl}/api/questions/${hr.idQuestion.value}/reponse/${hr.id.getOrElse(Reponse.Id(-100)).value}",
        "data=InputData()",
        headers = Map(Keys.tokenHeader -> dom.window.localStorage.getItem(Keys.tokenLoSto))
      )
      .debugLogErrors()

  val reponseObserver: Observer[HttpSimpleReponse] = Observer[HttpSimpleReponse](onNext = hr => {

    saveReponseByQuestion(hr)
  })

  def displaySingleReponses(idQuestion: Question.Id, reponses: Seq[HttpSimpleReponse]): Div = {
    div(
      cls := QcmIoCss.reponses.className.value,
      reponses
        .grouped(2)
        .map(
          lotReponses =>
            div(
              ul(
                lotReponses.map(
                  r =>
                    li(
                      label(r.label.value),
                      value(r.label.value),
                      input(
                        composeEvents(onClick)(e => e.flatMap(_ => saveReponseByQuestion(r))) --> { r =>
                          println(s"EVENT EVENT ${r.responseText}")
                        },
                        name := idQuestion.value.toString,
                        typ("radio")
                      )
                    )
                )
              )
            )
        )
        .toSeq
    )
  }

  def displayMultiReponses(idQuestion: Question.Id, reponses: Seq[HttpSimpleReponse]): Div = {
    div(
      cls := QcmIoCss.reponses.className.value,
      reponses
        .grouped(2)
        .map(
          lotReponses =>
            div(
              ul(
                lotReponses.map(
                  r =>
                    li(
                      label(r.label.value),
                      value(r.label.value),
                      input(
                        composeEvents(onClick)(e => e.flatMap(_ => saveReponseByQuestion(r))) --> { r =>
                          println(s"EVENT EVENT ${r.responseText}")
                        },
                        name := idQuestion.value.toString,
                        typ("checkbox")
                      )
                    )
                )
              )
            )
        )
        .toSeq
    )
  }

  def displayAreaReponse(idQuestion: Question.Id): Div = {
    div(
      cls := QcmIoCss.reponses.className.value
    )
  }

  val header: ReactiveHtmlElement[html.Div] = div(cls := QcmIoCss.headerCss.className.value, "Header")

  val httpquestionsObserver: Observer[Seq[HttpQuestion]] = Observer[Seq[HttpQuestion]](onNext = httpQuestions => {
    dom.console.info(s"HttpQuestion ${httpQuestions}")
    questionList.update(_ => httpQuestions)
  })

  def loadQuestions(gstate: QCMGlobalState): Modifier[Div] = {

    AjaxEventStream
      .get(
        s"${Configuration.backendUrl}/api/questions",
        headers = Map(Keys.tokenHeader -> dom.window.localStorage.getItem(Keys.tokenLoSto))
      )
      .map(
        r =>
          parse(r.responseText) match {
            case Right(json) => json.as[Seq[HttpQuestion]].getOrElse(Seq.empty[HttpQuestion])
            case Left(e) =>
              dom.console.error(s"parsing error ${e}")
              Seq.empty[HttpQuestion]

          }
      )
      .debugLogErrors() --> httpquestionsObserver

  }

}

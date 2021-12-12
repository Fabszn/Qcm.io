package pages

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.circe.parser._
import org.qcmio.Keys
import org.qcmio.front.{Configuration, QcmIoCss, WithGlobalState}
import org.qcmio.model.{HttpQuestion, HttpSimpleReponse}
import org.scalajs.dom
import org.scalajs.dom. html

object HomePage extends WithGlobalState {


  case class QcmState(token: Option[String] = None) {
    def getToken: String = token.getOrElse("None token available")
  }


  val questionList = Var(Seq.empty[HttpQuestion])

  def homePage(gstate: QCMGlobalState) = div(
    loadQuestions(gstate),
    cls := QcmIoCss.questions.className.value,
    children <-- questionList.signal.map{
      case Nil => List(div("None question available"))
      case qs => qs.map(h => displayQuestion(h))
    })


  def displayQuestion(httpQuestion: HttpQuestion): Div = {
  div(
      httpQuestion.label.value,
      displayReponses(httpQuestion.reponses)
    )
  }

  def displayReponses(reponses: Seq[HttpSimpleReponse]): Div = {
    div(
      cls := QcmIoCss.reponses.className.value,
      reponses.grouped(2).map(lotReponses => div(
        ul(
          lotReponses.map(r =>
            li(label(r.label.value), input(typ("radio"),name:="q1"))
          )
        ))
      ).toSeq)
  }

  val header: ReactiveHtmlElement[html.Div] = div(cls := QcmIoCss.headerCss.className.value, "Header")


  val httpquestionsObserver: Observer[Seq[HttpQuestion]] = Observer[Seq[HttpQuestion]](onNext = httpQuestions => {
    dom.console.info(s"HttpQuestion ${httpQuestions}")
    questionList.update(_ => httpQuestions)
  })




  def loadQuestions(gstate: QCMGlobalState): Modifier[Div] = {

    AjaxEventStream
      .get(s"${Configuration.backendUrl}/api/questions", headers = Map(Keys.tokenHeader -> dom.window.localStorage.getItem(Keys.tokenLoSto)))
      .map(r =>
        parse(r.responseText) match {
          case Right(json) => json.as[Seq[HttpQuestion]].getOrElse(Seq.empty[HttpQuestion])
          case Left(e) =>
            dom.console.error(s"parsing error ${e}")
            Seq.empty[HttpQuestion]

        }
      ).debugLogErrors() --> httpquestionsObserver


  }

}

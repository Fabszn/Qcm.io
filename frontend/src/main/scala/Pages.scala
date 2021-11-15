package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import io.circe.parser._
import io.circe.syntax._
import org.qcmio.Keys
import org.qcmio.auth.LoginInfo
import org.qcmio.front.QcmioRouter.HomePage
import org.qcmio.model.{HttpQuestion, HttpSimpleReponse}
import org.scalajs.dom
import org.scalajs.dom.{console, html}


object Pages extends WithGlobalState {

  case class LoginState(login: String = "", mdp: String = "")

  case class QcmState(token: Option[String] = None) {
    def getToken: String = token.getOrElse("None token available")
  }


  val stateVar = Var(LoginState(login = "fabszn@protonmail.com", mdp = "toto"))

  val eventsVar = Var(List.empty[String])

  def renderInputRow(mods: Modifier[HtmlElement]*): HtmlElement = {

    div(
      p(mods)
    )
  }

  val loginWriter = stateVar.updater[String]((state, login) => state.copy(login = login))

  val pwdWriter = stateVar.updater[String]((state, pass) => state.copy(mdp = pass))


  def loginPage(gState: QCMGlobalState) = div(
    cls := QcmIoCss.loginForm.className.value,
    renderInputRow(
      label("Login: "),
      input(
        placeholder("e-mail"),
        controlled(
          value <-- stateVar.signal.map(_.login),
          onInput.mapToValue --> loginWriter
        )
      )
    ),
    renderInputRow(
      label("pwd: "),
      input(
        controlled(
          value <-- stateVar.signal.map(_.mdp),
          onInput.mapToValue --> pwdWriter
        )
      )
    ),
    p(
      button(
        "Submit",
        composeEvents(onClick)(_.flatMap(_ => {
          AjaxEventStream
            .post(s"${Configuration.backendUrl}/api/login", LoginInfo(stateVar.signal.now.login, stateVar.signal.now.mdp).asJson.toString())
            .map(r => {
              dom.window.localStorage.setItem(Keys.tokenLoSto, r.getResponseHeader(Keys.tokenHeader))
              r.getResponseHeader(Keys.tokenHeader)
            }).recover {
            case err: AjaxStreamError => {
              console.log(err.getMessage)
              Some(err.getMessage)
            }
          }
        })) --> (
          (t: String) => {
            gState.update(_.copy(token = Some(t)))
            t
          }
          ).andThen(e => {
          console.log(s"message $e")
          QcmioRouter.router.pushState(HomePage)
        })

      )
    )

  )

  val questionList = Var(Seq.empty[HttpQuestion])

  def homePage(gstate: QCMGlobalState) = div(
    loadQuestions(gstate),
    cls := QcmIoCss.questions.className.value,
    children <-- questionList.signal.map(_.map(h => displayQuestion(h)))
  )


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
            li(r.label.value)
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

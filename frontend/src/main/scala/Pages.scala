package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import com.raquo.laminar.api.L._
import io.circe.syntax._
import org.qcmio.Keys
import org.qcmio.auth.User
import org.qcmio.front.QcmioRouter.LoginPage
import org.scalajs.dom


object Pages {

  case class LoginState(login: String = "", mdp: String = "")

  case class QcmState(token: Option[String] = None)


  val qcmAppState = Var(QcmState())
  val stateVar = Var(LoginState(login = "Michel", mdp = "toto"))

  val eventsVar = Var(List.empty[String])

  def renderInputRow(mods: Modifier[HtmlElement]*): HtmlElement = {

    div(
      p(mods)
    )
  }

  val loginWriter = stateVar.updater[String]((state, login) => state.copy(login = login))

  val pwdWriter = stateVar.updater[String]((state, pass) => state.copy(mdp = pass))


  val loginPage = div(
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
        composeEvents(onClick)(_.flatMap( _ => {
          dom.console.log("teest")
          AjaxEventStream
          .post(s"${Configuration.backendUrl}/api/login", User(stateVar.signal.now.login, stateVar.signal.now.mdp).asJson.toString())
          .map(r => {
            dom.window.localStorage.setItem(Keys.tokenLoSto, r.getResponseHeader(Keys.tokenHeader))
            r.getResponseHeader(Keys.tokenHeader)
            QcmioRouter.router.pushState(LoginPage)
          }).recover { case err: AjaxStreamError => Some(err.getMessage) }})) --> qcmAppState.updater[String] { case (state, token) => state.copy(token = Some(token)) }
      )
    )

  )

  val homePage = div(s"Home page${qcmAppState.now().token}")

}

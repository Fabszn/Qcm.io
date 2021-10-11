package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import com.raquo.laminar.api.L._
import io.circe.syntax._
import org.qcmio.Keys
import org.qcmio.auth.User
import org.qcmio.front.QcmioRouter.{HomePage, LoginPage}
import org.scalajs.dom


object Pages extends WithGlobalState {

  case class LoginState(login: String = "", mdp: String = "")

  case class QcmState(token: Option[String] = None)





  val stateVar = Var(LoginState(login = "Michel", mdp = "toto"))

  val eventsVar = Var(List.empty[String])

  def renderInputRow(mods: Modifier[HtmlElement]*): HtmlElement = {

    div(
      p(mods)
    )
  }

  val loginWriter = stateVar.updater[String]((state, login) => state.copy(login = login))

  val pwdWriter = stateVar.updater[String]((state, pass) => state.copy(mdp = pass))


  def loginPage(gState:QCMGlobalState) = div(
    p(
      input(value <-- gState.signal.map(_.token.getOrElse("no tokeken")))
    ),
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
          AjaxEventStream
          .post(s"${Configuration.backendUrl}/api/login", User(stateVar.signal.now.login, stateVar.signal.now.mdp).asJson.toString())
          .map(r => {
            dom.window.localStorage.setItem(Keys.tokenLoSto, r.getResponseHeader(Keys.tokenHeader))
            r.getResponseHeader(Keys.tokenHeader)

          }).recover {
            case err: AjaxStreamError => Some(err.getMessage)
          }})) --> ((t:String) =>  gState.update(_.copy(token = Some(t)))).andThen(_ => QcmioRouter.router.pushState(HomePage))

      )
    )

  )

  def homePage(gstate:QCMGlobalState) = div(s"Home page${gstate.signal.now().token}")

}

package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import com.raquo.laminar.api.L._
import io.circe.syntax._
import org.qcmio.Keys
import org.qcmio.auth.User
import org.qcmio.front.QcmioRouter.{HomePage, LoginPage}
import org.scalajs.dom


object Pages {

  case class LoginState(login: String = "", mdp: String = "")

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
        inContext(thisNode => {
          val $click = thisNode.events(onClick).sample(stateVar.signal)
          val $response = $click.flatMap { state =>
            AjaxEventStream
              .post(s"${Configuration.backendUrl}/api/login", User(state.login, state.mdp).asJson.toString())
              .map(r => {
                dom.window.localStorage.setItem("token", r.getResponseHeader(Keys.tokenHeader))
                "connected"
              })
              .recover { case err: AjaxStreamError => Some(err.getMessage) }
          }
          List(
            $click.map(
              opt => List(s"Les valeurs saisies : ${opt.login} / ${opt.mdp} et ensuite la réponse du server")
            ) --> eventsVar
          )
        })
      )
    )
  )

  val homePage = div("Home page")

}

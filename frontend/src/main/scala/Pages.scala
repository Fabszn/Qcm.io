package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import com.raquo.laminar.api.L._
import io.circe.generic.auto._
import io.circe.syntax._
import org.qcmio.auth.User




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
              .post("http://localhost:8088/api/login", User(state.login, state.mdp).asJson.toString())
              .map("Response: " + _.responseText)
              .recover { case err: AjaxStreamError => Some(err.getMessage) }
          }
          List(
            $click.map(
              opt => List(s"Les valeurs saisies : ${opt.login} / ${opt.mdp} et ensuite la réponse du server")
            ) --> eventsVar,
            $response --> eventsVar.updater[String](_ :+ _)
          )
        })
      )
    )
  )

}

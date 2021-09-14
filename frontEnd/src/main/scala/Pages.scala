package org.qcmio.front

import com.raquo.laminar.api.L._
import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError



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
    p(label("server Answer"),cls:=QcmIoCss.myStyles.className.value,input(value <-- eventsVar.signal.map(_.mkString(",")))),
    renderInputRow(
      label("Login: "),
      input(
        placeholder("12345"),
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
          val $response = $click.flatMap { _ =>
            AjaxEventStream
              .get("http://localhost:8080/qcm/admin")
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

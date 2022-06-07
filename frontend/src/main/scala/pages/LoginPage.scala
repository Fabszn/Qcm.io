package pages

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import com.raquo.laminar.api.L._
import io.circe.syntax._
import org.qcmio.Keys
import org.qcmio.auth.LoginInfo
import org.qcmio.front.QcmioRouter.HomePage
import org.qcmio.front.{Configuration, QcmIoCss, QcmioRouter, WithGlobalState}
import org.scalajs.dom
import org.scalajs.dom.{console, html}

object LoginPage extends WithGlobalState {

  case class LoginState(login: String = "", mdp: String = "")

  val stateVar = Var(LoginState(login = "fabszn@protonmail.com", mdp = "toto"))

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
            .post(
              s"${Configuration.backendUrl}/api/login",
              LoginInfo(stateVar.signal.now.login, stateVar.signal.now.mdp).asJson.toString()
            )
            .map(r => {
              dom.window.localStorage.setItem(Keys.tokenLoSto, r.getResponseHeader(Keys.tokenHeader))
              r.getResponseHeader(Keys.tokenHeader)
            })
            .recover {
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
          QcmioRouter.router.pushState(HomePage)
        })
      )
    )
  )

}

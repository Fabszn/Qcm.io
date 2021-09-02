import com.raquo.laminar.api.L._
import org.scalajs.dom

object Main {

  def main(args: Array[String]): Unit = {

    lazy val container = dom.document.getElementById("app-container")

    case class LoginState(login: String = "", mdp: String = "")


    val stateVar = Var(LoginState())

    val submitter = Observer[LoginState] { state =>

      dom.window.alert(s"Login: ${state.login}; pwd: ${state.mdp}")
    }

    def renderInputRow(mods: Modifier[HtmlElement]*): HtmlElement = {

      div(

        p(mods),

      )
    }

    val loginWriter = stateVar.updater[String]((state, login) => state.copy(login = login))

    val pwdWriter = stateVar.updater[String]((state, pass) => state.copy(mdp = pass))


    val app = div(
      form(
        onSubmit
          .preventDefault
          .mapTo(stateVar.now()) --> submitter
        ,

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
          button(typ("submit"), "Submit")
        )
      )
    )


    lazy val appElement = {
      div(
        div(
          label("Login : "),
          input(placeholder := "your login")
        ),
        div(
          label("password : "),
          input(placeholder := "your password")
        )
      )
    }
    renderOnDomContentLoaded(container, app)

  }


}

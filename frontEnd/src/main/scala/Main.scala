import org.scalajs.dom

import com.raquo.laminar.api.L._

object Main {

  def main(args: Array[String]): Unit = {

    lazy val container = dom.document.getElementById("app-container")

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
      renderOnDomContentLoaded(container, appElement)

  }


}

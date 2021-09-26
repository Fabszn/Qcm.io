package org.qcmio.front

import com.raquo.laminar.api.L._
import com.raquo.waypoint.root
import org.qcmio.front.QcmioRouter.LoginPage
import org.scalajs.dom
import scalacss.internal.mutable.GlobalRegistry


object QcmioMain {

  def main(args: Array[String]): Unit = {


    val CssSettings = scalacss.devOrProdDefaults
    import CssSettings._

    GlobalRegistry.addToDocumentOnRegistration()
    GlobalRegistry.register(QcmIoCss)

    val clickObserver = Observer[dom.MouseEvent](onNext = _ => QcmioRouter.router.pushState(LoginPage))

    lazy val container = dom.document.getElementById("app-container")

    val app: Div = div(
      h1("Routing App"),
      p(
        button(
          "Submit",
          onClick --> clickObserver,
           div("Hello Worl")
          )
        ),
        child <-- QcmioRouter.splitter.$view
      )

    renderOnDomContentLoaded(container, app)

  }

}

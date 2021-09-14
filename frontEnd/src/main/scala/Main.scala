package org.qcmio.front

import com.raquo.laminar.api.L._
import com.raquo.waypoint.root
import org.qcmio.front.QcmioRouter.LoginPage
import org.scalajs.dom
import scalacss.internal.mutable.GlobalRegistry


object Main {

  def main(args: Array[String]): Unit = {


    val CssSettings = scalacss.devOrProdDefaults
    import CssSettings._

    GlobalRegistry.addToDocumentOnRegistration()
    GlobalRegistry.register(QcmIoCss)

    lazy val container = dom.document.getElementById("app-container")

    val app: Div = div(
      h1("Routing App"),
      p(
        button(
          "Submit",
          inContext(thisNode => {
            val $click = thisNode.events(onClick)
            $click.flatMap { _ =>

              EventStream.fromValue(QcmioRouter.router.pushState(root /"main"/ 1/))

            }
           div("Hello Worl")
          })
        ),
        child <-- QcmioRouter.splitter.$view
      ))

    renderOnDomContentLoaded(container, app)

  }

}

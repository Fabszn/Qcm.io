package org.qcmio.front

import com.raquo.laminar.api.L._
import com.raquo.waypoint.root
import org.qcmio.front.QcmioRouter.{HomePage, LoginPage}
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
      child <-- QcmioRouter.splitter.$view
    )

    dom.document.addEventListener(
      "DOMContentLoaded", { (e: dom.Event) =>{
        val token = dom.window.localStorage.getItem("valeur")
        println(s"token -${token}-")
        println(s"token -${token}-")
        println(s"token -${token}-")
        println(s"token -${token}-")
        if (token == null) {
          println("test")
          QcmioRouter.router.pushState(HomePage)
        } else {
          QcmioRouter.router.pushState(LoginPage)
        }
        }
      }
    )

    renderOnDomContentLoaded(container, app)

  }

}

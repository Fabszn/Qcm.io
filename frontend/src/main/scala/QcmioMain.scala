package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.laminar.api.L._
import org.qcmio.Keys
import org.qcmio.front.Pages.QcmState
import org.qcmio.front.QcmioRouter.{HomePage, LoginPage}
import org.scalajs.dom
import scalacss.internal.mutable.GlobalRegistry

object QcmioMain {

  def main(args: Array[String]): Unit = {

    //val CssSettings = scalacss.devOrProdDefaults
    //import CssSettings._

    //GlobalRegistry.addToDocumentOnRegistration()
    GlobalRegistry.register(QcmIoCss)

    val qcmAppState = Var(QcmState())

    val checkTokenObserver: Observer[String] = Observer[String](onNext = status => {
      dom.console.info("Observer")
      if (status == "200")
        QcmioRouter.router.pushState(HomePage)
      else
        QcmioRouter.router.pushState(LoginPage)
    })

    lazy val container = dom.document.getElementById("app-container")

    val app: Div = div(
      child <-- QcmioRouter.splitter(qcmAppState).$view
    )


    dom.document.addEventListener(
      "DOMContentLoaded", { (e: dom.Event) => {
        val token = dom.window.localStorage.getItem(Keys.tokenLoSto)
        dom.console.info("Load load 33333")
        if (token == null) {
          QcmioRouter.router.pushState(LoginPage)
        } else {
          dom.console.info("Load load")
          (AjaxEventStream
            .get(s"${Configuration.backendUrl}/api/login/isValid")
            .map(r => {
              dom.console.info(r.status)
              r.status.toString
            }) --> checkTokenObserver )


        }

      }
      }
    )

    renderOnDomContentLoaded(container, app)

  }

}

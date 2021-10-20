package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStreamError
import com.raquo.laminar.api.L._
import com.raquo.waypoint.root
import org.qcmio.Keys
import org.qcmio.auth.LoginInfo
import org.qcmio.front.Pages.{QcmState, stateVar}
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

    //val clickObserver = Observer[dom.MouseEvent](onNext = _ => QcmioRouter.router.pushState(LoginPage))

    lazy val container = dom.document.getElementById("app-container")

    val app: Div = div(
      child <-- QcmioRouter.splitter(qcmAppState).$view
    )

    dom.document.addEventListener(
      "DOMContentLoaded", { (e: dom.Event) =>{
        val token = dom.window.localStorage.getItem(Keys.tokenLoSto)
        dom.console.info( "Load load 33333")
        if (token == null) {
          QcmioRouter.router.pushState(LoginPage)
        } else {
          dom.console.info( "Load load")
          /*AjaxEventStream
            .get(s"${Configuration.backendUrl}/api/login/isValid")
            .map(r => {
              dom.console.info( r.status)
              dom.window.localStorage.setItem(Keys.tokenLoSto, r.getResponseHeader(Keys.tokenHeader))
              r.getResponseHeader(Keys.tokenHeader)
            }).recover {
            case err: AjaxStreamError => Some(err.getMessage)
          }//.andThen(_ => QcmioRouter.router.pushState(HomePage))

          QcmioRouter.router.pushState(HomePage)
           */
        }

      }
      }
    )

    renderOnDomContentLoaded(container, app)

  }

}

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
    val initModifier: Modifier[Div] = {
      val token = dom.window.localStorage.getItem(Keys.tokenLoSto)
      if (token == null) {
        QcmioRouter.router.pushState(LoginPage)
        emptyMod
      } else {
        AjaxEventStream
          .get(s"${Configuration.backendUrl}/api/login/isValid")
          .map(r => {
           r.status.toString
          }).debugLogErrors() --> checkTokenObserver

      }
    }

    val app: Div = div(
      initModifier,
      child <-- QcmioRouter.splitter(qcmAppState).$view
    )








    renderOnDomContentLoaded(container, app)

  }


}

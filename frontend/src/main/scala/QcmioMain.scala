package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStatusError
import com.raquo.laminar.api.L._
import org.qcmio.Keys
import pages.HomePage.QcmState
import org.qcmio.front.QcmioRouter.{HomePage, LoginPage}
import org.scalajs.dom
import scalacss.internal.mutable.GlobalRegistry

object QcmioMain {

  def main(args: Array[String]): Unit = {

    val CssSettings = scalacss.devOrProdDefaults
    import CssSettings._
    GlobalRegistry.addToDocumentOnRegistration()
    GlobalRegistry.register(QcmIoCss)

    val qcmAppState = Var(QcmState())

    val checkTokenObserver: Observer[Int] = Observer[Int](onNext = status => {
      dom.console.info("Observer")
      if (status == 200)
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
          .get(s"${Configuration.backendUrl}/api/login/isValid", headers = Map(Keys.tokenHeader -> token))
          .map(r =>
           r.status
          ).recover{
          case e:AjaxStatusError =>
            QcmioRouter.router.pushState(LoginPage)
            Some(e.status)
        }.debugLogErrors() --> checkTokenObserver

      }
    }

    val app: Div = div(
      initModifier,
      child <-- QcmioRouter.splitter(qcmAppState).$view
    )


    renderOnDomContentLoaded(container, app)

  }


}

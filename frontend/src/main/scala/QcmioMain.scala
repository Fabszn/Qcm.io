package org.qcmio.front

import com.raquo.airstream.web.AjaxEventStream
import com.raquo.airstream.web.AjaxEventStream.AjaxStatusError
import com.raquo.laminar.api.L._
import io.laminext.syntax.tailwind
import io.laminext.tailwind.theme.DefaultTheme
//import io.laminext.syntax.tailwind.{Modal, ModalContent}
import org.qcmio.Keys
import pages.HomePage.QcmState
import org.qcmio.front.QcmioRouter.{HomePage, LoginPage}
import org.scalajs.dom
import scalacss.internal.mutable.GlobalRegistry
import io.laminext.syntax.tailwind._
import io.laminext.tailwind.theme.Theme
import io.laminext.tailwind.modal.Modal

object QcmioMain {


  val modalContent: Var[Option[tailwind.ModalContent]] = Var[Option[ModalContent]](Option.empty)


  def main(args: Array[String]): Unit = {

    val CssSettings = scalacss.devOrProdDefaults
    import CssSettings._
    GlobalRegistry.addToDocumentOnRegistration()
    GlobalRegistry.register(QcmIoCss)
    Theme.setTheme(DefaultTheme.theme)


    val qcmAppState = Var(QcmState())

    val checkTokenObserver: Observer[Int] = Observer[Int](onNext = status => {
      dom.console.info("Observer")
      if (status == 200)
        QcmioRouter.router.pushState(HomePage)
      else
        QcmioRouter.router.pushState(LoginPage)
    })






    lazy val container = dom.document.querySelector("#app-container")
    lazy val modalContainer = dom.document.querySelector("#modal-container")


    val initModifier: Modifier[Div] = {
      val token = dom.window.localStorage.getItem(Keys.tokenLoSto)
      if (token == null) {
        QcmioRouter.router.pushState(LoginPage)
        emptyMod
      } else {
        AjaxEventStream
          .get(s"${Configuration.backendUrl}/api/login/isValid", headers = Map(Keys.tokenHeader -> token))
          .map(r => r.status)
          .recover {
            case e: AjaxStatusError =>
              QcmioRouter.router.pushState(LoginPage)
              Some(e.status)
          }
          .debugLogErrors() --> checkTokenObserver

      }
    }


    val app: Div = div(
      initModifier,
      child <-- QcmioRouter.splitter(qcmAppState).$view
    )

    val _ = documentEvents.onDomContentLoaded.foreach { _ =>
      Modal.initialize()


      render(modalContainer,  TW.modal(modalContent.signal,Theme.current.modal.customize(
        contentWrapTransition = _.customize(
          nonHidden = _ :+ "bg-gray-900"
        )
      )))

      render(container, app)
    }(unsafeWindowOwner)


  }

}

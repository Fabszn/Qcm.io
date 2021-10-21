package org.qcmio.front

import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.waypoint._
import upickle.default._
import org.scalajs.dom.html.Div


object QcmioRouter extends WithGlobalState {

  sealed trait Page
  case class MainPage(userId: Int) extends Page
  case object LoginPage extends Page
  case object HomePage extends Page
  case object EntryPage extends Page

  implicit val UserPageRW: ReadWriter[MainPage] = macroRW
  implicit val rw: ReadWriter[Page] = macroRW

  val loginRoute = Route.static(LoginPage, root / "login" / endOfSegments)
  val homeRoute = Route.static(HomePage, root / "home" / endOfSegments)
  val entryPoint = Route.static(EntryPage, root / "qcm" / "index.html"/ endOfSegments)

  val router = new Router[Page](
    routes = List(entryPoint, loginRoute, homeRoute),
    getPageTitle = _.toString, // mock page title (displayed in the browser tab next to favicon)
    serializePage = page => write(page)(rw), // serialize page data for storage in History API log
    deserializePage = pageStr => read(pageStr)(rw) // deserialize the above
  )(
    $popStateEvent = windowEvents.onPopState, // this is how Waypoint avoids an explicit dependency on Laminar
    owner = unsafeWindowOwner // this router will live as long as the window
  )

  def splitter(gState:QCMGlobalState) = SplitRender[Page, HtmlElement](router.$currentPage)
    .collectStatic(LoginPage) { Pages.loginPage(gState) }
    .collectStatic(HomePage) { Pages.homePage(gState) }
    .collectStatic(EntryPage) { div() }

  def renderMainPage(): ReactiveHtmlElement[Div] = {
    div(
      "User page "
    )
  }

}

package org.qcmio.environment.http

import cats.data.NonEmptyList
import cats.effect.Blocker
import org.http4s.CacheDirective.`no-cache`
import org.http4s.{Charset, HttpRoutes, MediaType}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{`Cache-Control`, `Content-Type`}
import org.http4s.server.Router
import scalatags.Text
import zio.RIO
import zio.interop.catz._
import org.http4s.server.staticcontent.{FileService, fileService}

import java.util.concurrent.Executors

final class IndexEndpoint[R] {


  type IndexTask[A] = RIO[R, A]

  val blockingPool = Executors.newSingleThreadExecutor()
  val blocker = Blocker.liftExecutorService(blockingPool)
  val dsl = Http4sDsl[IndexTask]



  private val prefixPath = "/"




  val routes: HttpRoutes[IndexTask] = Router(
    prefixPath ->
  )


}

object indexHtml {
  import scalatags.Text.all._
  def t: Text.TypedTag[String] = html(
    head(
      script(src:="/qcm/front/0.1.0-SNAPSHOT/front-fastopt.js")
    ),
    body(
      div( id :="app-container")
    )
  )
}

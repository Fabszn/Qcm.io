package org.qcmio.environment.http

import cats.effect.Blocker
import org.http4s.{HttpRoutes, Request, StaticFile}
import org.http4s.dsl.Http4sDsl

import zio.RIO
import zio.interop.catz._

import java.util.concurrent.Executors

final class StaticResourcesEndpoint[R] {

  val blockingPool = Executors.newSingleThreadExecutor()
  val blocker = Blocker.liftExecutorService(blockingPool)
  type StaticResTask[A] = RIO[R, A]


  val dsl = Http4sDsl[StaticResTask]


  import org.http4s.server.staticcontent.webjarService
  import org.http4s.server.staticcontent.WebjarService.{WebjarAsset, Config}

  def isJsAsset(asset: WebjarAsset): Boolean =
    asset.asset.endsWith(".js") || asset.asset.endsWith(".html")

  val routes: HttpRoutes[StaticResTask] =  webjarService(
    Config(
      filter = isJsAsset,
      blocker = blocker
    )
  )


}

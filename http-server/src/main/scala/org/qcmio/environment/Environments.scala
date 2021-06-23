package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import zio.ULayer
import zio.clock.Clock

object Environments {

  type HttpEnvironment = Configuration with Clock
  type AppEnvironment = HttpEnvironment

  val httpServerEnvironment: ULayer[HttpEnvironment] =
    Configuration.live ++ Clock.live
  val appEnvironment: ULayer[AppEnvironment] = httpServerEnvironment

}

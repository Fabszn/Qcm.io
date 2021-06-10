package org.qcmio.environment

import org.qcmio.environment.config.Configuration
import zio.clock.Clock

object Environments {

  type HttpEnvironment = Configuration with Clock
  type AppEnvironment = HttpEnvironment

  val httpServerEnvironment = Configuration.live ++ Clock.live
  val appEnvironment = httpServerEnvironment

}

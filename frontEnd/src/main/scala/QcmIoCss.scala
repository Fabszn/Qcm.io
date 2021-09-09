package org.qcmio.front

import scalacss.DevDefaults._

object QcmIoCss extends StyleSheet.Inline {
  import dsl._
  val myStyles = style(
    backgroundColor(Color("red"))
  )


  val loginForm = style(
    display.flex,
    justifyContent.center,
    alignItems.center,
    flexDirection.column,
    backgroundColor(Color("gray"))
  )
}



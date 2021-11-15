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

  val headerCss = style(
    display.flex,
    justifyContent.center,
    alignItems.flexStart,
    backgroundColor(Color("gray")),
    borderColor(Color("gray"))
  )


  val questions = style(display.flex,flexDirection.column)
  val reponses = style(
    display.flex,
    flexDirection.row,
    justifyContent.center
  )
}



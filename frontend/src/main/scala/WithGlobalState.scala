package org.qcmio.front

import com.raquo.airstream.state.Var
import pages.HomePage.QcmState

trait WithGlobalState {

  type QCMGlobalState = Var[QcmState]


}

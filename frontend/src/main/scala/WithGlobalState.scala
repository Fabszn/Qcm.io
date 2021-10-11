package org.qcmio.front

import com.raquo.airstream.state.Var
import org.qcmio.front.Pages.QcmState

trait WithGlobalState {

  type QCMGlobalState = Var[QcmState]


}

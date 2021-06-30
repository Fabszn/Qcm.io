package org.qcmio.environment.repository

import doobie.quill.DoobieContext
import io.getquill.SnakeCase

private[repository] trait DBContext{
  val ctx = new QcmIODBContext
}

private[repository] class QcmIODBContext extends  DoobieContext.Postgres(SnakeCase){



}




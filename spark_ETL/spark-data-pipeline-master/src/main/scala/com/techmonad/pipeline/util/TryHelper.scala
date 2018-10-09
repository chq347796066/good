package com.techmonad.pipeline.util

import scala.util.control.NonFatal


trait TryHelper {

  def withTry[T](t: => T): Option[T] =
    try
      Option(t)
    catch {
      case NonFatal(th) =>
        th.printStackTrace()
        None
    }


}

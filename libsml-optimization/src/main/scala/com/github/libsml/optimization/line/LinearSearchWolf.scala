package com.github.libsml.optimization.line

import com.github.libsml.math.function.Function
import com.github.libsml.math.linalg
import com.github.libsml.math.linalg._
import com.github.libsml.math.linalg.BLAS._

/**
 * Created by huangyu on 15/8/26.
 */
class LinearSearchWolf(val param: LineSearchParameter) extends LineSearch {

  //  val param: LineSearchParameter = new LineSearchParameter()

  override def search(w: linalg.Vector, f: Double, g: linalg.Vector, s: linalg.Vector, stp: Double,
                      function: Function,wp:Vector): (Int, Double, Double) = {
    var count: Int = 0
    var width: Double = 0
    var dg: Double = 0
    var finit: Double = 0
    var dginit: Double = 0
    var dgtest: Double = 0
    val dec: Double = 0.5f
    val inc: Double = 2.1f
    var step = stp
    var fnew = f

    /* Check the input parameters for errors. */
    if (stp <= 0) {
      throw new IllegalStateException("Line search")
    }

    /* Compute the initial gradient in the search direction. */
    dginit = dot(g, s)

    /* Make sure that s points to a descent direction. */
    if (dginit > 0) {
      throw new IllegalStateException("Line search:INCREASEGRADIENT")
    }

    /* The initial value of the objective function. */
    finit = f
    dgtest = param.ftol * dginit
    while (true) {

      copy(wp, w)
      axpy(step, s, w)
      if (!function.isInBound(w)) {
        width = dec
      } else {
        fnew = function.gradient(w, g)
        count += 1
        if (!function.isInBound(w) || fnew > finit + step * dgtest) {
          width = dec
        } else {
          /* Check the Wolfe condition. */
          dg = dot(g, s)
          if (dg < param.wolfe * dginit) {
            width = inc
          } else {
            return (count, fnew, step)
          }
        }

      }
      if (step < param.minStep) {
        throw new IllegalStateException("LBFGSERR_MINIMUMSTEP")
      }
      if (step > param.maxStep) {
        throw new IllegalStateException("LBFGSERR_MAXIMUMSTEP")
      }
      if (param.maxLinesearch <= count) {
        throw new IllegalStateException("LBFGSERR_MAXIMUMLINESEARCH")
      }
      step *= width
//      println("step:"+step)
    }
    return (count, fnew, step)
  }
}
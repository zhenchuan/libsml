package com.github.libsml.driver.optimization

import com.github.libsml.model.Model

/**
 * Created by huangyu on 15/9/5.
 */
trait OptimizationAlgorithm[P, W] {

  def optimize(): Model[P, W]

}

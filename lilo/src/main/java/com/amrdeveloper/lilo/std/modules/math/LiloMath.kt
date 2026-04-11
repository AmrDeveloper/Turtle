package com.amrdeveloper.lilo.std.modules.math

import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloModule

private const val MODULE_NAME = "math"

private const val TAU = 6.283185307179586.toFloat()

val liloMathModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "inf", value = LiloFloat(value = Float.POSITIVE_INFINITY))
    it.setAttr(name = "nan", value = LiloFloat(value = Float.NaN))
    it.setAttr(name = "pi", value = LiloFloat(value = Math.PI.toFloat()))
    it.setAttr(name = "tau", value = LiloFloat(value = TAU))
}

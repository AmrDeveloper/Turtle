package com.amrdeveloper.lilo.std.modules

import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloModule

private const val MODULE_NAME = "math"

val liloMathModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "inf", value = LiloFloat(value = Float.POSITIVE_INFINITY))
    it.setAttr(name = "nan", value = LiloFloat(value = Float.NaN))
}

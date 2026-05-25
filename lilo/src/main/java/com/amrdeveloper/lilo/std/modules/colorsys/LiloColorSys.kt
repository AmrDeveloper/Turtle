package com.amrdeveloper.lilo.std.modules.colorsys

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloModule
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloTuple
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFunctionType
import kotlin.math.abs

private const val MODULE_NAME = "colorsys"

val liloColorSysModule = LiloModule(name = MODULE_NAME).also {
    // Some floating-point constants
    it.setAttr(name = "ONE_THIRD", value = LiloFloat(value = 1.0f/3.0f))
    it.setAttr(name = "ONE_SIXTH", value = LiloFloat(value = 1.0f/6.0f))
    it.setAttr(name = "TWO_THIRD", value = LiloFloat(value = 2.0f/3.0f))

    // Functions
    it.setAttr(name = "hsv_to_rgb", value = LiloColorSysHSVToRGB)
}

object LiloColorSysHSVToRGB : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 3) {
            return LiloResult.Failure(error = RuntimeException("`hsv_to_rgb` expect 3 arguments (h, s, v) but got ${args.size}`"))
        }

        val hArg = args[0]
        val sArg = args[1]
        val vArg = args[2]
        if (hArg !is LiloFloat || sArg !is LiloFloat || vArg !is LiloFloat) {
            return LiloResult.Failure(error = RuntimeException("`hsv_to_rgb` expect 3 arguments (h, s, v) with float type"))
        }

        val h = hArg.value
        val s = sArg.value
        val v = vArg.value

        val h6 = h * 6f

        val c = v * s
        val x = c * (1 - abs(x = (h6 % 2f) - 1))
        val m = v - c

        var r = 0f
        var g = 0f
        var b = 0f

        when {
            h6 < 1 -> {
                r = c
                g = x
            }
            h6 < 2 -> {
                r = x
                g = c
            }
            h6 < 3 -> {
                g = c
                b = x
            }
            h6 < 4 -> {
                g = x
                b = c
            }
            h6 < 5 -> {
                r = x
                b = c
            }
            else -> {
                r = c
                b = x
            }
        }

        val red   = LiloInt(value = ((r + m) * 255).toInt())
        val green = LiloInt(value =((g + m) * 255).toInt())
        val blue  = LiloInt(value =((b + m) * 255).toInt())
        return LiloResult.Success(data = LiloTuple(values = listOf(red, green, blue),))
    }
}

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
import kotlin.math.truncate

private const val MODULE_NAME = "colorsys"

val liloColorSysModule = LiloModule(name = MODULE_NAME).also {
    // Some floating-point constants
    it.setAttr(name = "ONE_THIRD", value = LiloFloat(value = 1.0/3.0))
    it.setAttr(name = "ONE_SIXTH", value = LiloFloat(value = 1.0/6.0))
    it.setAttr(name = "TWO_THIRD", value = LiloFloat(value = 2.0/3.0))

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
        if ((hArg !is LiloInt && hArg !is LiloFloat)
            || (sArg !is LiloInt && sArg !is LiloFloat)
            || (vArg !is LiloInt && vArg !is LiloFloat) ) {
            return LiloResult.Failure(error = RuntimeException("`hsv_to_rgb` expect 3 arguments (h, s, v) with float type"))
        }

        val h = if (hArg is LiloInt) hArg.value.toDouble() else (hArg as LiloFloat).value
        val s = if (sArg is LiloInt) sArg.value.toDouble() else (sArg as LiloFloat).value
        val v = if (vArg is LiloInt) vArg.value.toDouble() else (vArg as LiloFloat).value
        if (s == 0.0) {
            val rgb = listOf(LiloFloat(v), LiloFloat(v), LiloFloat(v))
            return LiloResult.Success(data = LiloTuple(values = rgb))
        }

        var i = truncate((h * 6.0)).toInt()
        val f = (h * 6.0) - i
        val p = v * (1.0 - s).toFloat()
        val q = v * (1.0 - s * f).toFloat()
        val t = v * (1.0 - s * (1.0 - f)).toFloat()
        i %= 6
        when (i) {
            0 -> {
                val rgb = listOf(LiloFloat(v), LiloFloat(t), LiloFloat(p))
                return LiloResult.Success(data = LiloTuple(values = rgb))
            }
            1 -> {
                val rgb = listOf(LiloFloat(q), LiloFloat(v), LiloFloat(p))
                return LiloResult.Success(data = LiloTuple(values = rgb))
            }
            2 -> {
                val rgb = listOf(LiloFloat(p), LiloFloat(v), LiloFloat(t))
                return LiloResult.Success(data = LiloTuple(values = rgb))
            }
            3 -> {
                val rgb = listOf(LiloFloat(p), LiloFloat(q), LiloFloat(v))
                return LiloResult.Success(data = LiloTuple(values = rgb))
            }
            4 -> {
                val rgb = listOf(LiloFloat(t), LiloFloat(p), LiloFloat(v))
                return LiloResult.Success(data = LiloTuple(values = rgb))
            }
            5 -> {
                val rgb = listOf(LiloFloat(v), LiloFloat(p), LiloFloat(q))
                return LiloResult.Success(data = LiloTuple(values = rgb))
            }
        }
        return LiloResult.Failure(error = RuntimeException("`hsv_to_rgb` unexpected values"))
    }
}

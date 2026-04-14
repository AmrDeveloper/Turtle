package com.amrdeveloper.lilo.std.modules.math

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloModule
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFunctionType
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import kotlin.math.tanh

private const val MODULE_NAME = "math"

private const val TAU = 6.283185307179586.toFloat()

val liloMathModule = LiloModule(name = MODULE_NAME).also {
    // Constants
    it.setAttr(name = "inf", value = LiloFloat(value = Float.POSITIVE_INFINITY))
    it.setAttr(name = "nan", value = LiloFloat(value = Float.NaN))
    it.setAttr(name = "pi", value = LiloFloat(value = Math.PI.toFloat()))
    it.setAttr(name = "tau", value = LiloFloat(value = TAU))

    // Functions
    it.setAttr(name = "sin", value = LiloMathSin)
    it.setAttr(name = "cos", value = LiloMathCos)
    it.setAttr(name = "tan", value = LiloMathTan)
    it.setAttr(name = "tanh", value = LiloMathTanh)
}

object LiloMathSin : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val argument = args[0]
        if (argument !is LiloFloat) {
            return LiloResult.Failure(error = RuntimeException("`math.sin` expect `float` type but got `${argument.type.toString()}`"))
        }
        val sin = sin(argument.value)
        return LiloResult.Success(data = LiloFloat(value = sin))
    }
}

object LiloMathCos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val argument = args[0]
        if (argument !is LiloFloat) {
            return LiloResult.Failure(error = RuntimeException("`math.cos` expect `float` type but got `${argument.type.toString()}`"))
        }
        val sin = cos(argument.value)
        return LiloResult.Success(data = LiloFloat(value = sin))
    }
}

object LiloMathTan : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val argument = args[0]
        if (argument !is LiloFloat) {
            return LiloResult.Failure(error = RuntimeException("`math.tan` expect `float` type but got `${argument.type.toString()}`"))
        }
        val sin = tan(argument.value)
        return LiloResult.Success(data = LiloFloat(value = sin))
    }
}


object LiloMathTanh : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val argument = args[0]
        if (argument !is LiloFloat) {
            return LiloResult.Failure(error = RuntimeException("`math.tanh` expect `float` type but got `${argument.type.toString()}`"))
        }
        val sin = tanh(argument.value)
        return LiloResult.Success(data = LiloFloat(value = sin))
    }
}

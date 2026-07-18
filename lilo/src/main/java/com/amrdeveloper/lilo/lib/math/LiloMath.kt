package com.amrdeveloper.lilo.lib.math

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.objects.LiloFloat
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import kotlin.math.tanh

private const val MODULE_NAME = "math"

private const val TAU = 6.283185307179586

val liloMathModule = LiloModule(name = MODULE_NAME).also {
    // Constants
    it.setAttr(name = "inf", value = LiloFloat(value = Double.POSITIVE_INFINITY))
    it.setAttr(name = "nan", value = LiloFloat(value = Double.NaN))
    it.setAttr(name = "pi", value = LiloFloat(value = Math.PI))
    it.setAttr(name = "tau", value = LiloFloat(value = TAU))

    // Functions
    it.setAttr(name = "sin", value = LiloMathSin)
    it.setAttr(name = "cos", value = LiloMathCos)
    it.setAttr(name = "tan", value = LiloMathTan)
    it.setAttr(name = "tanh", value = LiloMathTanh)
    it.setAttr(name = "atan2", value = LiloMathAtan2)
    it.setAttr(name = "sqrt", value = LiloMathSqrt)
    it.setAttr(name = "radians", value = LiloMathRadians)
    it.setAttr(name = "floor", value = LiloMathFloor)
    it.setAttr(name = "ceil", value = LiloMathCeil)
}

object LiloMathSin : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.sin` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if (argument !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`math.sin` expect `float` type but got `${argument.type.toString()}`")
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
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.cos` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if (argument !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`math.cos` expect `float` type but got `${argument.type.toString()}`")
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
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.tan` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if (argument !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`math.tan` expect `float` type but got `${argument.type.toString()}`")
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
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.tanh` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if (argument !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`math.tanh` expect `float` type but got `${argument.type.toString()}`")
        }

        val sin = tanh(argument.value)
        return LiloResult.Success(data = LiloFloat(value = sin))
    }
}

object LiloMathAtan2 : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`math.atan2` expected 2 argument, got ${args.size}")
        }

        val x = args[0]
        val y = args[0]
        if (x !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`math.atan2` expect x parameter to be `float` type but got `${x.type.toString()}`")
        }

        if (y !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`math.atan2` expect y parameter to be `float` type but got `${y.type.toString()}`")
        }

        val result = atan2(x.value, y.value)
        return LiloResult.Success(data = LiloFloat(value = result))
    }
}

object LiloMathSqrt : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.sqrt` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if ((argument !is LiloFloat) && (argument !is LiloInt)) {
            throw createLiloException(liloTypeErrorType, "`math.sqrt` expect `float` or `int` type but got `${argument.type.toString()}`")
        }

        val radians = when (argument) {
            is LiloInt -> sqrt(x = argument.value.toDouble())
            is LiloFloat -> sqrt(x = argument.value)
            else -> 0.0
        }
        return LiloResult.Success(data = LiloFloat(value = radians))
    }
}

object LiloMathFloor : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.floor` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        val magicFloorFunction = argument.getAttr(name = LiloMagicMethod.FLOOR)
        if ((magicFloorFunction == null) || magicFloorFunction !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "`${argument}` doesn't support __floor__")
        }

        val floorResult = magicFloorFunction.invoke(interpreter, args = listOf(argument))
        if (floorResult.isFailure()) {
            throw createLiloException(liloTypeErrorType, "floor function failed with input `${argument}")
        }
        val round = floorResult.toSuccessData()
        return LiloResult.Success(data = round)
    }
}

object LiloMathCeil : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.ceil` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        val magicCeilFunction = argument.getAttr(name = LiloMagicMethod.CEIL)
        if ((magicCeilFunction == null) || magicCeilFunction !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "`${argument}` doesn't support __ceil__")
        }

        val ceilResult = magicCeilFunction.invoke(interpreter, args = listOf(argument))
        if (ceilResult.isFailure()) {
            throw createLiloException(liloTypeErrorType, "ceil function failed with input `${argument}")
        }
        val round = ceilResult.toSuccessData()
        return LiloResult.Success(data = round)
    }
}

object LiloMathRadians : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`math.radians` expected 1 argument, got ${args.size}")
        }

        val argument = args[0]
        if ((argument !is LiloFloat) && argument !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`math.radians` expect `float` or `int` type but got `${argument.type.toString()}`")
        }

        val radians = when (argument) {
            is LiloInt -> ((argument.value * PI)/ 180.0)
            is LiloFloat -> (argument.value * PI / 180.0)
            else -> 0.0
        }
        return LiloResult.Success(data = LiloFloat(value = radians))
    }
}

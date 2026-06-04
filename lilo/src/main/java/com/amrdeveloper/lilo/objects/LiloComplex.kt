package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloComplexType =
    LiloType(name = "complex", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = ComplexInit)

        // Binary
        it.setAttr(name = LiloMagicMethod.ADD, value = ComplexAdd)
        it.setAttr(name = LiloMagicMethod.SUB, value = ComplexSub)

        it.setAttr(name = "real", value = ComplexReal)
        it.setAttr(name = "imag", value = ComplexImag)
    }

private object ComplexInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size > 2) {
            return LiloResult.Failure(error = LiloExceptionMessage("complex() takes at most 2 arguments (${args.size} given)"))
        }

        var real = 0.0
        if (args.isNotEmpty()) {
            real = when {
                args[0] is LiloInt -> (args[0] as LiloInt).value.toDouble()
                args[0] is LiloFloat -> (args[0] as LiloFloat).value
                else -> {
                    return LiloResult.Failure(error = LiloExceptionMessage("complex() argument 1 must be a number"))
                }
            }
        }

        var imag = 0.0
        if (args.size > 1) {
            imag = when {
                args[1] is LiloInt -> (args[1] as LiloInt).value.toDouble()
                args[1] is LiloFloat -> (args[1] as LiloFloat).value
                else -> {
                    return LiloResult.Failure(error = LiloExceptionMessage(" argument 'imag' must be a real number, not ${args[1].type}"))
                }
            }
        }

        return LiloResult.Success(data = LiloComplex(real, imag))
    }
}

private object ComplexAdd : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            return LiloResult.Failure(error = LiloExceptionMessage("complex add expects 2 arguments, got ${args.size}"))
        }

        if (args[0] !is LiloComplex || args[1] !is LiloComplex) {
            return LiloResult.Failure(error = LiloExceptionMessage("complex add expects 2 complex arguments, got ${args[0].type} and ${args[1].type}"))
        }

        val lhs = args[0] as LiloComplex
        val rhs = args[1] as LiloComplex
        return LiloResult.Success(data = LiloComplex(lhs.real + rhs.real, lhs.imag + rhs.imag))
    }
}

private object ComplexSub : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            return LiloResult.Failure(error = LiloExceptionMessage("complex add expects 2 arguments, got ${args.size}"))
        }

        if (args[0] !is LiloComplex || args[1] !is LiloComplex) {
            return LiloResult.Failure(error = LiloExceptionMessage("complex add expects 2 complex arguments, got ${args[0].type} and ${args[1].type}"))
        }

        val lhs = args[0] as LiloComplex
        val rhs = args[1] as LiloComplex
        return LiloResult.Success(data = LiloComplex(lhs.real - rhs.real, lhs.imag - rhs.imag))
    }
}

private object ComplexReal : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1 || args[0] !is LiloComplex) {
            return LiloResult.Failure(error = LiloExceptionMessage("complex real expects 0 arguments, got ${args.size - 1}"))
        }
        val lhs = args[0] as LiloComplex
        return LiloResult.Success(data = LiloFloat(value = lhs.real))
    }
}

private object ComplexImag : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1 || args[0] !is LiloComplex) {
            return LiloResult.Failure(error = LiloExceptionMessage("complex real expects 0 arguments, got ${args.size - 1}"))
        }
        val lhs = args[0] as LiloComplex
        return LiloResult.Success(data = LiloFloat(value = lhs.imag))
    }
}

data class LiloComplex(val real: Double, val imag: Double) : LiloObject(liloComplexType) {
    override fun toString() = "($real+${imag}j)"
}

package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
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
            throw createLiloException(liloTypeErrorType, "`complex.__init__` Expect at most 2 arguments got ${args.size}")
        }

        var real = 0.0
        if (args.isNotEmpty()) {
            real = when {
                args[0] is LiloInt -> (args[0] as LiloInt).value.toDouble()
                args[0] is LiloFloat -> (args[0] as LiloFloat).value
                else -> {
                    throw createLiloException(liloTypeErrorType, "`complex.__init__` Expect first argument to be number, got ${args[0].type}")
                }
            }
        }

        var imag = 0.0
        if (args.size > 1) {
            imag = when {
                args[1] is LiloInt -> (args[1] as LiloInt).value.toDouble()
                args[1] is LiloFloat -> (args[1] as LiloFloat).value
                else -> {
                    throw createLiloException(liloTypeErrorType, "`complex.__init__` Expect second argument to be number, got ${args[0].type}")
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
            throw createLiloException(liloTypeErrorType, "`complex.__add__` Expects 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloComplex) {
            throw createLiloException(liloTypeErrorType, "`complex.__add__` Expect first argument to be complex, got ${args[1].type}")
        }

        if (args[1] !is LiloComplex) {
            throw createLiloException(liloTypeErrorType, "`complex.__add__` Expect second argument to be complex, got ${args[1].type}")
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
            throw createLiloException(liloTypeErrorType, "`complex.__sub__` Expects 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloComplex) {
            throw createLiloException(liloTypeErrorType, "`complex.__sub__` Expect first argument to be complex, got ${args[1].type}")
        }

        if (args[1] !is LiloComplex) {
            throw createLiloException(liloTypeErrorType, "`complex.__sub__` Expect second argument to be complex, got ${args[1].type}")
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
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`complex.real` Expects 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloComplex) {
            throw createLiloException(liloTypeErrorType, "`complex.real` Expects first argument to be complex, got ${args[0].type}")
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
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`complex.imag` Expects 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloComplex) {
            throw createLiloException(liloTypeErrorType, "`complex.imag` Expects first argument to be complex, got ${args[0].type}")
        }

        val lhs = args[0] as LiloComplex
        return LiloResult.Success(data = LiloFloat(value = lhs.imag))
    }
}

data class LiloComplex(val real: Double, val imag: Double) : LiloObject(liloComplexType) {
    override fun toString() = "($real+${imag}j)"
}

package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import kotlin.math.absoluteValue
import kotlin.math.pow

val liloFloatType = LiloType(name = "float", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    // Binary
    it.setAttr(name = LiloMagicMethod.ADD, value = FloatAdd)
    it.setAttr(name = LiloMagicMethod.SUB, value = FloatSub)
    it.setAttr(name = LiloMagicMethod.MUL, value = FloatMul)
    it.setAttr(name = LiloMagicMethod.TRUE_DIV, value = FloatTrueDiv)
    it.setAttr(name = LiloMagicMethod.POW, value = FloatPow)

    it.setAttr(name = LiloMagicMethod.ABS, value = FloatAbs)

    // Comparisons
    it.setAttr(name = LiloMagicMethod.EQ, value = FloatEQ)
    it.setAttr(name = LiloMagicMethod.NE, value = FloatNE)
    it.setAttr(name = LiloMagicMethod.GT, value = FloatGT)
    it.setAttr(name = LiloMagicMethod.GE, value = FloatGE)
    it.setAttr(name = LiloMagicMethod.LT, value = FloatLT)
    it.setAttr(name = LiloMagicMethod.LE, value = FloatLE)

    // Unary
    it.setAttr(name = LiloMagicMethod.POS, value = FloatPos)
    it.setAttr(name = LiloMagicMethod.NEG, value = FloatNeg)

    it.setAttr(name = LiloMagicMethod.BOOL, value = FloatBool)
}

private object FloatAdd : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__add__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__add__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__add__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value + rhs.value))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value + rhs.value))
            else -> LiloResult.Success(data = LiloFloat(value = 0.0))
        }
    }
}

private object FloatSub : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__sub__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__sub__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__sub__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value - rhs.value))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value - rhs.value))
            else -> LiloResult.Success(data = LiloFloat(value = 0.0))
        }
    }
}

private object FloatMul : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__mul__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__mul__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__mul__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value * rhs.value))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value * rhs.value))
            else -> LiloResult.Success(data = LiloFloat(value = 0.0))
        }
    }
}

private object FloatTrueDiv : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__truediv__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__truediv__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__truediv__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        return when (val rhs = args[1]) {
            is LiloInt -> {
                if (rhs.value == 0) {
                    throw createLiloException(liloZeroDivisionErrorType, "division by zero")
                }
                LiloResult.Success(data = LiloFloat(value = lhs.value / rhs.value))
            }
            is LiloFloat -> {
                if (rhs.value == 0.0) {
                    throw createLiloException(liloZeroDivisionErrorType, "division by zero")
                }
                LiloResult.Success(data = LiloFloat(value = lhs.value / rhs.value))
            }
            else -> LiloResult.Success(data = LiloFloat(value = 0.0))
        }
    }
}

private object FloatPow : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__pow__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__pow__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__pow__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value.pow(rhs.value)))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value.pow(rhs.value)))
            else -> LiloResult.Success(data = LiloFloat(value = 0.0))
        }
    }
}

private object FloatAbs : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`float.__abs__` Expect 1 argument got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__abs__` Expect argument to be float, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        return LiloResult.Success(data = LiloFloat(value = lhs.value.absoluteValue))
    }
}

private object FloatEQ : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__eq__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__eq__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__eq__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        val rhsValue = when (val rhs = args[1]) {
            is LiloFloat -> rhs.value
            is LiloInt -> rhs.value.toDouble()
            else -> 0.0
        }
        return LiloResult.Success(data = LiloBool(value = lhs.value == rhsValue))
    }
}

private object FloatNE : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__ne__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__ne__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__ne__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        val rhsValue = when (val rhs = args[1]) {
            is LiloFloat -> rhs.value
            is LiloInt -> rhs.value.toDouble()
            else -> 0.0
        }
        return LiloResult.Success(data = LiloBool(value = lhs.value != rhsValue))
    }
}


private object FloatGT : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__gt__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__gt__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__gt__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        val rhsValue = when (val rhs = args[1]) {
            is LiloFloat -> rhs.value
            is LiloInt -> rhs.value.toDouble()
            else -> 0.0
        }
        return LiloResult.Success(data = LiloBool(value = lhs.value > rhsValue))
    }
}

private object FloatGE : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__ge__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__ge__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__ge__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        val rhsValue = when (val rhs = args[1]) {
            is LiloFloat -> rhs.value
            is LiloInt -> rhs.value.toDouble()
            else -> 0.0
        }
        return LiloResult.Success(data = LiloBool(value = lhs.value >= rhsValue))
    }
}

private object FloatLT : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__lt__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__lt__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__lt__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        val rhsValue = when (val rhs = args[1]) {
            is LiloFloat -> rhs.value
            is LiloInt -> rhs.value.toDouble()
            else -> 0.0
        }
        return LiloResult.Success(data = LiloBool(value = lhs.value < rhsValue))
    }
}

private object FloatLE : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`float.__le__` Expect at most 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__le__` Expect first argument to be float, got ${args[0].type}")
        }

        if (args[1] !is LiloFloat && args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`float.__le__` Expect second argument to be number, got ${args[0].type}")
        }

        val lhs = args[0] as LiloFloat
        val rhsValue = when (val rhs = args[1]) {
            is LiloFloat -> rhs.value
            is LiloInt -> rhs.value.toDouble()
            else -> 0.0
        }
        return LiloResult.Success(data = LiloBool(value = lhs.value <= rhsValue))
    }
}

private object FloatPos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`float.__pos__` Expect at most 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__pos__` Expect argument to be float, got ${args[0].type}")
        }

        val operand = args[0] as LiloFloat
        return LiloResult.Success(data = operand)
    }
}

private object FloatNeg : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`float.__neg__` Expect at most 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__neg__` Expect argument to be float, got ${args[0].type}")
        }

        val operand = args[0] as LiloFloat
        return LiloResult.Success(data = LiloFloat(value = -operand.value))
    }
}

private object FloatBool : LiloObject(liloFloatType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`float.__bool__` Expect at most 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`float.__bool__` Expect argument to be float, got ${args[0].type}")
        }

        val self = args[0] as LiloFloat
        return LiloResult.Success(data = LiloBool(value = self.value != 0.0))
    }
}

data class LiloFloat(val value: Double) : LiloObject(liloFloatType) {
    override fun toString(): String {
        if (value == Double.POSITIVE_INFINITY) return "inf"
        if (value.isNaN()) return "nan"
        return value.toString()
    }
}

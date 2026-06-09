package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import kotlin.math.pow

val liloIntType = LiloType(name = "int", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.INIT, value = IntInit)

    // Binary
    it.setAttr(name = LiloMagicMethod.ADD, value = IntAdd)
    it.setAttr(name = LiloMagicMethod.SUB, value = IntSub)
    it.setAttr(name = LiloMagicMethod.MUL, value = IntMul)
    it.setAttr(name = LiloMagicMethod.TRUE_DIV, value = IntTrueDiv)
    it.setAttr(name = LiloMagicMethod.FLOOR_DIV, value = IntFloorDiv)
    it.setAttr(name = LiloMagicMethod.MOD, value = IntMod)
    it.setAttr(name = LiloMagicMethod.POW, value = IntPow)

    // Shifts
    it.setAttr(name = LiloMagicMethod.RIGHT_SHIFT, value = IntRightShift)
    it.setAttr(name = LiloMagicMethod.LEFT_SHIFT, value = IntLeftShift)

    it.setAttr(name = LiloMagicMethod.BIT_AND, value = IntBitAnd)
    it.setAttr(name = LiloMagicMethod.BIT_OR, value = IntBitOr)
    it.setAttr(name = LiloMagicMethod.BIT_XOR, value = IntBitXor)

    // Comparisons
    it.setAttr(name = LiloMagicMethod.EQ, value = IntEq)
    it.setAttr(name = LiloMagicMethod.NE, value = IntNotEq)
    it.setAttr(name = LiloMagicMethod.GT, value = IntGT)
    it.setAttr(name = LiloMagicMethod.GE, value = IntGE)
    it.setAttr(name = LiloMagicMethod.LT, value = IntLT)
    it.setAttr(name = LiloMagicMethod.LE, value = IntLE)

    // Unary
    it.setAttr(name = LiloMagicMethod.POS, value = IntPos)
    it.setAttr(name = LiloMagicMethod.NEG, value = IntNeg)

    // Bool
    it.setAttr(name = LiloMagicMethod.BOOL, value = IntBool)
}

private object IntInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`int.__init__` Expect at most 1 arguments got ${args.size}")
        }

        val argument = args[0]
        if (argument is LiloBool) return LiloResult.Success(data = LiloInt(value = if (argument.value) 1 else 0))
        if (argument is LiloInt) return LiloResult.Success(data = argument)
        if (argument is LiloFloat) return LiloResult.Success(data = LiloInt(value = argument.value.toInt()))
        throw createLiloException(liloTypeErrorType, "`int.__init__` argument must be bool, int or float, got ${args[0].type}")
    }
}

private object IntAdd : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__add__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__add__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt && args[1] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`int.__add__` Expect second argument to be number, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloInt(value = lhs.value + rhs.value))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value + rhs.value))
            else -> LiloResult.Success(data = LiloInt(value = 0))
        }
    }
}

private object IntSub : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__sub__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__sub__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt && args[1] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`int.__sub__` Expect second argument to be number, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloInt(value = lhs.value - rhs.value))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value - rhs.value))
            else -> LiloResult.Success(data = LiloInt(value = 0))
        }
    }
}

private object IntMul : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__mul__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__mul__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt && args[1] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`int.__mul__` Expect second argument to be number, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloInt(value = lhs.value * rhs.value))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value * rhs.value))
            else -> LiloResult.Success(data = LiloInt(value = 0))
        }
    }
}

private object IntTrueDiv : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__truediv__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__truediv__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt && args[1] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`int.__truediv__` Expect second argument to be number, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        return when (val rhs = args[1]) {
            is LiloInt -> {
                if (rhs.value == 0) {
                    throw createLiloException(liloZeroDivisionErrorType, "division by zero")
                }
                LiloResult.Success(data = LiloInt(value = lhs.value / rhs.value))
            }
            is LiloFloat -> {
                if (rhs.value == 0.0) {
                    throw createLiloException(liloZeroDivisionErrorType, "division by zero")
                }
                LiloResult.Success(data = LiloFloat(value = lhs.value / rhs.value))
            }
            else -> LiloResult.Success(data = LiloInt(value = 0))
        }
    }
}

private object IntFloorDiv : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__floordiv__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__floordiv__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt && args[1] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`int.__floordiv__` Expect second argument to be number, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        return when (val rhs = args[1]) {
            is LiloInt -> {
                if (rhs.value == 0) {
                    throw createLiloException(liloZeroDivisionErrorType, "division by zero")
                }
                LiloResult.Success(data = LiloInt(value = lhs.value.floorDiv(rhs.value)))
            }
            is LiloFloat -> {
                if (rhs.value == 0.0) {
                    throw createLiloException(liloZeroDivisionErrorType, "division by zero")
                }
                LiloResult.Success(data = LiloInt(value = lhs.value.floorDiv( rhs.value.toInt())))
            }
            else -> LiloResult.Success(data = LiloInt(value = 0))
        }
    }
}

private object IntMod : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__mod__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__mod__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt && args[1] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`int.__mod__` Expect second argument to be number, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloInt(value = lhs.value.mod(rhs.value)))
            is LiloFloat -> LiloResult.Success(data = LiloInt(value = lhs.value.mod( rhs.value.toInt())))
            else -> LiloResult.Success(data = LiloInt(value = 0))
        }
    }
}

private object IntPow : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__pow__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__pow__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt && args[1] !is LiloFloat) {
            throw createLiloException(liloTypeErrorType, "`int.__pow__` Expect second argument to be number, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        return when (val rhs = args[1]) {
            is LiloInt -> LiloResult.Success(data = LiloInt(value = lhs.value.toDouble().pow(rhs.value).toInt()))
            is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value.toDouble().pow( rhs.value)))
            else -> LiloResult.Success(data = LiloInt(value = 0))
        }
    }
}

private object IntRightShift : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__shr__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__shr__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__shr__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloInt(value = lhs.value shr rhs.value))
    }
}

private object IntLeftShift : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__shl__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__shl__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__shl__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloInt(value = lhs.value shl rhs.value))
    }
}

private object IntBitAnd : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__and__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__and__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__and__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloInt(value = lhs.value and rhs.value))
    }
}

private object IntBitOr : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__or__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__or__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__or__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloInt(value = lhs.value or rhs.value))
    }
}

private object IntBitXor : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__xor__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__xor__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__xor__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloInt(value = lhs.value xor rhs.value))
    }
}

private object IntEq : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__eq__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__eq__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = false))
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloBool(value = lhs.value == rhs.value))
    }
}

private object IntNotEq : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__ne__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__ne__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = true))
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloBool(value = lhs.value != rhs.value))
    }
}

private object IntGT : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__gt__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__gt__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__gt__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloBool(value = lhs.value > rhs.value))
    }
}

private object IntGE : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__ge__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__ge__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__ge__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloBool(value = lhs.value >= rhs.value))
    }
}

private object IntLT : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__lt__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__lt__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__lt__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloBool(value = lhs.value < rhs.value))
    }
}

private object IntLE : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`int.__le__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__le__` Expect first argument to be int, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__le__` Expect second argument to be int, got ${args[1].type}")
        }

        val lhs = args[0] as LiloInt
        val rhs = args[1] as LiloInt
        return LiloResult.Success(data = LiloBool(value = lhs.value <= rhs.value))
    }
}

private object IntPos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`int.__pos__` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__pos__` Expect argument to be int, got ${args[0].type}")
        }

        val operand = args[0] as LiloInt
        return LiloResult.Success(data = operand)
    }
}

private object IntNeg : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`int.__neg__` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__neg__` Expect argument to be int, got ${args[0].type}")
        }

        val operand = args[0] as LiloInt
        return LiloResult.Success(data = LiloInt(value = -operand.value))
    }
}

private object IntBool : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`int.__bool__` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`int.__bool__` Expect argument to be int, got ${args[0].type}")
        }

        val self = args[0] as LiloInt
        return LiloResult.Success(data = LiloBool(value = self.value != 0))
    }
}

data class LiloInt(val value: Int) : LiloObject(liloIntType) {
    override fun toString() = value.toString()
}

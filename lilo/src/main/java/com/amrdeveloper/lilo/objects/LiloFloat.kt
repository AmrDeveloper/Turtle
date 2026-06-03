package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloFloatType = LiloType(name = "float", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    // Binary
    it.setAttr(name = LiloMagicMethod.ADD, value = FloatAdd)
    it.setAttr(name = LiloMagicMethod.SUB, value = FloatSub)
    it.setAttr(name = LiloMagicMethod.MUL, value = FloatMul)
    it.setAttr(name = LiloMagicMethod.TRUE_DIV, value = FloatDiv)

    // Comparisons
    it.setAttr(name = LiloMagicMethod.GT, value = FloatGT)

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
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloFloat && (rhs is LiloInt || rhs is LiloFloat)) {
            return when (rhs) {
                is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value + rhs.value))
                is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value + rhs.value))
                else -> LiloResult.Failure(error = LiloExceptionMessage("Op `+` is unsupported between lhs & rhs"))
            }
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `+` is unsupported between lhs & rhs"))
    }
}

private object FloatSub : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloFloat && (rhs is LiloInt || rhs is LiloFloat)) {
            return when (rhs) {
                is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value - rhs.value))
                is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value - rhs.value))
                else -> LiloResult.Failure(error = LiloExceptionMessage("Op `-` is unsupported between lhs & rhs"))
            }
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `-` is unsupported between lhs & rhs"))
    }
}

private object FloatMul : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloFloat && (rhs is LiloInt || rhs is LiloFloat)) {
            return when (rhs) {
                is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value * rhs.value))
                is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value * rhs.value))
                else -> LiloResult.Failure(error = LiloExceptionMessage("Op `*` is unsupported between lhs & rhs"))
            }
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `*` is unsupported between lhs & rhs"))
    }
}

private object FloatDiv : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloFloat && (rhs is LiloInt || rhs is LiloFloat)) {
            return when (rhs) {
                is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value / rhs.value))
                is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value / rhs.value))
                else -> LiloResult.Failure(error = LiloExceptionMessage("Op `/` is unsupported between lhs & rhs"))
            }
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `/` is unsupported between lhs & rhs"))
    }
}

private object FloatGT : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0] as LiloFloat
        val rhsValue = when (val rhs = args[1]) {
            is LiloFloat -> rhs.value
            is LiloInt -> rhs.value.toDouble()
            else -> 0.0
        }
        return LiloResult.Success(data = LiloBool(value = lhs.value > rhsValue))
    }
}


private object FloatPos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val operand = args[0]
        if (operand is LiloFloat) {
            return LiloResult.Success(data = operand)
        }
        return LiloResult.Failure(error = LiloExceptionMessage("descriptor '__pos__' requires a 'float' object but received a '${operand.type}'"))
    }
}

private object FloatNeg : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val operand = args[0]
        if (operand is LiloFloat) {
            return LiloResult.Success(data = LiloFloat(value = -operand.value))
        }
        return LiloResult.Failure(error = LiloExceptionMessage("descriptor '__neg__' requires a 'float' object but received a '${operand.type}'"))
    }
}

private object FloatBool : LiloObject(liloFloatType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
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

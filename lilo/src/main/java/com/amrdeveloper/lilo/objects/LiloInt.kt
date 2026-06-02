package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloIntType = LiloType(name = "int", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.INIT, value = IntInit)

    // Binary
    it.setAttr(name = LiloMagicMethod.ADD, value = IntAdd)
    it.setAttr(name = LiloMagicMethod.SUB, value = IntSub)
    it.setAttr(name = LiloMagicMethod.MUL, value = IntMul)
    it.setAttr(name = LiloMagicMethod.DIV, value = IntDiv)
    it.setAttr(name = LiloMagicMethod.MOD, value = IntMod)

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
}

private object IntInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val argument = args[0]
        if (argument is LiloBool) return LiloResult.Success(data = LiloInt(value = if (argument.value) 1 else 0))
        if (argument is LiloInt) return LiloResult.Success(data = argument)
        if (argument is LiloFloat) return LiloResult.Success(data = LiloInt(value = argument.value.toInt()))
        return LiloResult.Failure(error = LiloExceptionMessage("Op `__init__` is unsupported with argument ${argument.type}"))
    }
}

private object IntAdd : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && (rhs is LiloInt || rhs is LiloFloat)) {
            return when (rhs) {
                is LiloInt -> LiloResult.Success(data = LiloInt(value = lhs.value + rhs.value))
                is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value + rhs.value))
                else -> LiloResult.Failure(error = LiloExceptionMessage("Op `+` is unsupported between lhs & rhs"))
            }
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `+` is unsupported between lhs & rhs"))
    }
}

private object IntSub : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloInt(value = lhs.value - rhs.value))
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `-` is unsupported between lhs & rhs"))
    }
}

private object IntMul : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && (rhs is LiloInt || rhs is LiloFloat)) {
            return when (rhs) {
                is LiloInt -> LiloResult.Success(data = LiloInt(value = lhs.value * rhs.value))
                is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value * rhs.value))
                else -> LiloResult.Failure(error = LiloExceptionMessage("Op `*` is unsupported between lhs & rhs"))
            }
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `*` is unsupported between lhs & rhs"))
    }
}

private object IntDiv : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && (rhs is LiloInt || rhs is LiloFloat)) {
            return when (rhs) {
                is LiloInt -> LiloResult.Success(data = LiloFloat(value = lhs.value.toDouble() / rhs.value))
                is LiloFloat -> LiloResult.Success(data = LiloFloat(value = lhs.value.toDouble() / rhs.value))
                else -> LiloResult.Failure(error = LiloExceptionMessage("Op `/` is unsupported between lhs & rhs"))
            }
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `/` is unsupported between lhs & rhs"))
    }
}

private object IntMod : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloInt(value = lhs.value % rhs.value))
        }
        return LiloResult.Failure(error = LiloExceptionMessage("Op `%` is unsupported between lhs & rhs"))
    }
}

private object IntEq : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = lhs.value == rhs.value))
        }
        return LiloResult.Success(data = LiloBool(value = false))
    }
}

private object IntNotEq : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = lhs.value != rhs.value))
        }
        return LiloResult.Success(data = LiloBool(value = true))
    }
}

private object IntGT : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = lhs.value > rhs.value))
        }
        return LiloResult.Success(data = LiloBool(value = true))
    }
}

private object IntGE : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = lhs.value >= rhs.value))
        }
        return LiloResult.Success(data = LiloBool(value = true))
    }
}

private object IntLT : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = lhs.value < rhs.value))
        }
        return LiloResult.Success(data = LiloBool(value = true))
    }
}

private object IntLE : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloBool(value = lhs.value <= rhs.value))
        }
        return LiloResult.Success(data = LiloBool(value = true))
    }
}

private object IntPos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val operand = args[0]
        if (operand is LiloInt) {
            return LiloResult.Success(data = operand)
        }
        return LiloResult.Failure(error = LiloExceptionMessage("descriptor '__pos__' requires a 'int' object but received a '${operand.type}'"))
    }
}

private object IntNeg : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val operand = args[0]
        if (operand is LiloInt) {
            return LiloResult.Success(data = LiloInt(value = -operand.value))
        }
        return LiloResult.Failure(error = LiloExceptionMessage("descriptor '__neg__' requires a 'int' object but received a '${operand.type}'"))
    }
}

data class LiloInt(val value: Int) : LiloObject(liloIntType) {

    init {
        setAttr(name = LiloMagicMethod.BOOL, value = IntBool)
    }

    override fun toString() = value.toString()
}

private object IntBool : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloInt
        return LiloResult.Success(data = LiloBool(value = self.value != 0))
    }
}

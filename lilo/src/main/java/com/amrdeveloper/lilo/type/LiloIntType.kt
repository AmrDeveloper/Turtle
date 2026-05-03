package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloBool
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloIntType = LiloType(name = "int", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.INIT, value = IntInit)

    it.setAttr(name = LiloMagicMethod.ADD, value = IntAdd)
    it.setAttr(name = LiloMagicMethod.SUB, value = IntSub)
    it.setAttr(name = LiloMagicMethod.MUL, value = IntMul)
    it.setAttr(name = LiloMagicMethod.DIV, value = IntDiv)
    it.setAttr(name = LiloMagicMethod.MOD, value = IntMod)

    it.setAttr(name = LiloMagicMethod.EQ, value = IntEq)
    it.setAttr(name = LiloMagicMethod.NOT_EQ, value = IntNotEq)

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
        return LiloResult.Failure(error = LiloException("Op `__init__` is unsupported with argument ${argument.type}"))
    }
}

private object IntAdd : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloInt(value = lhs.value + rhs.value))
        }
        return LiloResult.Failure(error = LiloException("Op `+` is unsupported between lhs & rhs"))
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
        return LiloResult.Failure(error = LiloException("Op `-` is unsupported between lhs & rhs"))
    }
}

private object IntMul : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloInt(value = lhs.value * rhs.value))
        }
        return LiloResult.Failure(error = LiloException("Op `*` is unsupported between lhs & rhs"))
    }
}

private object IntDiv : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val rhs = args[1]
        if (lhs is LiloInt && rhs is LiloInt) {
            return LiloResult.Success(data = LiloInt(value = lhs.value / rhs.value))
        }
        return LiloResult.Failure(error = LiloException("Op `/` is unsupported between lhs & rhs"))
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
        return LiloResult.Failure(error = LiloException("Op `%` is unsupported between lhs & rhs"))
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

private object IntPos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val operand = args[0]
        if (operand is LiloInt) {
            return LiloResult.Success(data = operand)
        }
        return LiloResult.Failure(error = LiloException("descriptor '__pos__' requires a 'int' object but received a '${operand.type}'"))
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
        return LiloResult.Failure(error = LiloException("descriptor '__neg__' requires a 'int' object but received a '${operand.type}'"))
    }
}

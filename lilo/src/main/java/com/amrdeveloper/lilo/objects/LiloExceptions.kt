package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.runtime.LiloRaise

const val EXCEPTION_CAUSE_FIELD = "__cause__"

val liloBaseExceptionType =
    LiloType(name = "BaseException", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = BaseExceptionInit)
        it.setAttr(name = LiloMagicMethod.STR, value = BaseExceptionStr)
    }

private object BaseExceptionInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> = LiloResult.Success(data = LiloObject(liloBaseExceptionType))
}

private object BaseExceptionStr : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0]
        val exceptionArgs =
            self.getAttr("args") ?: return LiloResult.Success(data = LiloStr(value = ""))
        if (exceptionArgs is LiloTuple) {
            val argsList = exceptionArgs.values
            val stringBuilder = StringBuilder()
            for (arg in argsList) {
                val string = arg.str(interpreter).valueOr { return it.toFailure() }
                stringBuilder.append(string)
            }
            return LiloResult.Success(data = LiloStr(value = stringBuilder.toString()))
        }
        val string = exceptionArgs.str(interpreter).valueOr { return it.toFailure() }
        return LiloResult.Success(data = LiloStr(value = string))
    }
}

val liloExceptionType =
    LiloType(name = "Exception", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloSyntaxErrorType =
    LiloType(name = "SyntaxError", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloAttributeErrorType =
    LiloType(name = "AttributeError", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloNameErrorType =
    LiloType(name = "NameError", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloUnboundLocalErrorType =
    LiloType(name = "UnboundLocalError", bases = listOf(liloNameErrorType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloImportErrorType = LiloType(name = "ImportError", bases = listOf(liloBaseExceptionType)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

val liloModuleNotFoundErrorType =
    LiloType(name = "ModuleNotFoundError", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloTypeErrorType =
    LiloType(name = "TypeError", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloValueErrorType =
    LiloType(name = "ValueError", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloRuntimeErrorType =
    LiloType(name = "RuntimeError", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloArithmeticErrorType =
    LiloType(name = "ArithmeticError", bases = listOf(liloExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloAssertionErrorType =
    LiloType(name = "AssertionError", bases = listOf(liloExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloNotImplementedError =
    LiloType(name = "NotImplementedError", bases = listOf(liloExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloStopIterationType =
    LiloType(name = "StopIteration", bases = listOf(liloExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloZeroDivisionErrorType =
    LiloType(name = "ZeroDivisionError", bases = listOf(liloArithmeticErrorType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

fun createLiloException(type: LiloType, vararg args: String): LiloRaise {
    val exceptionOjb = LiloObject(type)
    val exceptionArgs = mutableListOf<LiloObject>()
    exceptionArgs.add(LiloStr(value = type.name + ":"))
    for (arg in args) exceptionArgs.add(LiloStr(value = arg))
    val argsTuple = LiloTuple(values = exceptionArgs)
    exceptionOjb.setAttr(name = "args", value = argsTuple)
    return LiloRaise(exceptionOjb)
}

fun createLiloException(type: LiloType, vararg args: LiloObject): LiloRaise {
    val exceptionOjb = LiloObject(type)
    val exceptionArgs = mutableListOf<LiloObject>()
    exceptionArgs.add(LiloStr(value = type.name + ":"))
    for (arg in args) exceptionArgs.add(arg)
    val argsTuple = LiloTuple(values = exceptionArgs)
    exceptionOjb.setAttr(name = "args", value = argsTuple)
    return LiloRaise(exceptionOjb)
}

fun createLiloException(exceptionOjb: LiloObject): LiloRaise {
    val exceptionArgs = mutableListOf<LiloObject>()
    exceptionArgs.add(LiloStr(value = exceptionOjb.type?.name + ":"))
    return LiloRaise(exceptionOjb)
}

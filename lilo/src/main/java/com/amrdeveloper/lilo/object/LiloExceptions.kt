package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.LiloBaseType
import com.amrdeveloper.lilo.type.LiloType
import com.amrdeveloper.lilo.type.liloFunctionType

const val EXCEPTION_CAUSE_FIELD = "__cause__"

val liloBaseExceptionType =
    LiloType(name = "BaseException", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = BaseExceptionInit)
    }

private object BaseExceptionInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> = LiloResult.Success(data = LiloObject(liloBaseExceptionType))
}

val liloExceptionType =
    LiloType(name = "Exception", bases = listOf(liloBaseExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloAssertionErrorType =
    LiloType(name = "AssertionError", bases = listOf(liloExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

val liloStopIteratorType =
    LiloType(name = "StopIterator", bases = listOf(liloExceptionType)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

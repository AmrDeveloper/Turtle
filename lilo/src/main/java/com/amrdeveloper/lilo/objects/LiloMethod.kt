package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloMethodType =
    LiloType(name = "method", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

data class LiloMethod(val self: LiloObject, val method: LiloObject) :
    LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (method !is LiloCallable) return LiloResult.Failure(error = LiloExceptionMessage("Method is not callable"))
        val fullArgs = listOf(self) + args
        return method.invoke(interpreter, fullArgs)
    }
}

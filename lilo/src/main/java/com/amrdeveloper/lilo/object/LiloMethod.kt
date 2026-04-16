package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloMethodType

data class LiloMethod(val self: LiloObject, val method: LiloObject) :
    LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (method !is LiloCallable) return LiloResult.Failure(error = LiloException("Method is not callable"))
        val fullArgs = listOf(self) + args
        return method.invoke(interpreter, fullArgs)
    }
}

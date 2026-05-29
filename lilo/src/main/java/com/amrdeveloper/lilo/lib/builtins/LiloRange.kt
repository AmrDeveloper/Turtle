package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloRange
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloRangeFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val stop = args[0]
        if (stop !is LiloInt) {
            return LiloResult.Failure(error = LiloExceptionMessage(message = "Expect `Int` as `range` argument"))
        }
        return LiloResult.Success(data = LiloRange(stop = stop.value))
    }
}

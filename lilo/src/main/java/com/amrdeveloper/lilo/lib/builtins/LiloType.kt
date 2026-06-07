package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloTypeFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`type` Expect 1 argument, got ${args.size}")
        }
        return LiloResult.Success<LiloObject>(data = args[0].type!!)
    }
}

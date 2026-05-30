package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloFunction
import com.amrdeveloper.lilo.objects.LiloKernal
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloGPUDecorator : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1 || args[0] !is LiloFunction) {
            return LiloResult.Failure(error = RuntimeException("`gpu.kernal` expects one argument which is function"))
        }
        val function = args[0] as LiloFunction
        return LiloResult.Success(data = LiloKernal(definition = function.definition))
    }
}

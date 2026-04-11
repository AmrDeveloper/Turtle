package com.amrdeveloper.lilo.std.modules.gpu

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFunctionType

object LiloGPUMaxThreadsPerBlock : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val gpu = interpreter.liloMachine.getGPU()
        if (gpu == null) return LiloResult.Failure(error = RuntimeException("No GPU found"))
        val maxThreadsPerBlock = gpu.getGPUDevice().getLimits().maxComputeInvocationsPerWorkgroup
        return LiloResult.Success(data = LiloInt(value = maxThreadsPerBlock))
    }
}

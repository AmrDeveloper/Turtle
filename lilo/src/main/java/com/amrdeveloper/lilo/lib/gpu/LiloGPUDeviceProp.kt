package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloList
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.objects.liloFunctionType

object LiloGPUMaxThreadsPerBlock : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val gpu = interpreter.liloMachine.getGPU()
            ?: return LiloResult.Failure(error = RuntimeException("No GPU found"))
        val maxThreadsPerBlock = gpu.getGPUDevice().getLimits().maxComputeInvocationsPerWorkgroup
        return LiloResult.Success(data = LiloInt(value = maxThreadsPerBlock))
    }
}

object LiloGPUMaxThreadsDim : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val gpu = interpreter.liloMachine.getGPU()
            ?: return LiloResult.Failure(error = RuntimeException("No GPU found"))
        val gpuLimits = gpu.getGPUDevice().getLimits()
        val maxThreadsDimX = gpuLimits.maxComputeWorkgroupSizeX
        val maxThreadsDimY = gpuLimits.maxComputeWorkgroupSizeY
        val maxThreadsDimZ = gpuLimits.maxComputeWorkgroupSizeZ
        val maxThreadsDimArray = mutableListOf<LiloObject>(
            LiloInt(value = maxThreadsDimX),
            LiloInt(value = maxThreadsDimY),
            LiloInt(value = maxThreadsDimZ)
        )
        val maxThreadsDim = LiloList(values = maxThreadsDimArray)
        return LiloResult.Success(data = maxThreadsDim)
    }
}

object LiloGPUWrapSize : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val gpu = interpreter.liloMachine.getGPU()
            ?: return LiloResult.Failure(error = RuntimeException("No GPU found"))
        val wrapSize = gpu.getGPUDevice().getAdapterInfo().subgroupMaxSize
        return LiloResult.Success(data = LiloInt(value = wrapSize))
    }
}

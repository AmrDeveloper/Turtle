package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloMethodType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloLaunchConfigType = LiloType(name = "gpu.LaunchConfig", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE))
    .also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = GPULaunchConfigInitInit)
        it.setAttr(name = LiloMagicMethod.STR, value = GPULaunchConfigStr)
    }

private object GPULaunchConfigInitInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[0] !is LiloGPUDim || args[1] !is LiloGPUDim) {
            return LiloResult.Failure(error = LiloExceptionMessage("`gpu.LaunchConfig` expects 2 arguments (blocks=gpu.Dim, threads=gpu.Dim)"))
        }
        val launchConfig = LiloLaunchConfig(
            blocksDim = args[0] as LiloGPUDim,
            threadsDim = args[1] as LiloGPUDim
        )
        return LiloResult.Success(data = launchConfig)
    }
}

private object GPULaunchConfigStr : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloLaunchConfig
        val str = "(blocks=${self.blocksDim}, threads=${self.threadsDim})"
        return LiloResult.Success(data = LiloStr(value = str))
    }
}

data class LiloLaunchConfig(
    val blocksDim: LiloGPUDim,
    val threadsDim: LiloGPUDim,
) : LiloObject(liloLaunchConfigType) {
    override fun toString() = "gpu.LaunchConfig(${blocksDim}, ${threadsDim})"

}

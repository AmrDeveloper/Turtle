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
        if (args.size != 2 || args[0].type != liloDimType || args[1].type != liloDimType) {
            return LiloResult.Failure(error = LiloExceptionMessage("`gpu.LaunchConfig` expects 2 arguments (blocks=gpu.Dim, threads=gpu.Dim)"))
        }

        val launchConfig = LiloObject(type = liloLaunchConfigType)
        launchConfig.setAttr(name = "blocks", value = args[0])
        launchConfig.setAttr(name = "threads", value = args[1])
        return LiloResult.Success(data = launchConfig)
    }
}

private object GPULaunchConfigStr : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0]
        val blocks = self.getAttr(name = "blocks")
        val threads = self.getAttr(name = "threads")
        val str = "(blocks=${blocks}, threads=${threads})"
        return LiloResult.Success(data = LiloStr(value = str))
    }
}

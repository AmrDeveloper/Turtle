package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloFunction
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloTuple
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloKernalType =
    LiloType(name = "gpu.kernal", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = KernalInit)
        it.setAttr(name = LiloMagicMethod.GET_ITEM, value = KernalConfig)
        it.setAttr(name = LiloMagicMethod.CALL, value = KernalCall)
    }

private object KernalInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1 || args[0] !is LiloFunction) {
            throw createLiloException(liloTypeErrorType, "`gpu.Kernal` expects one argument which is function")
        }
        val function = args[0] as LiloFunction
        return LiloResult.Success(data = LiloKernal(definition = function.definition))
    }
}

private object KernalConfig : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[0] !is LiloKernal || args[1] !is LiloTuple) {
            throw createLiloException(liloTypeErrorType, "Kernal `[]` self, (blocks, threads)")
        }

        val config = args[1] as LiloTuple
        val values = config.values
        if (values.size != 2 || values[0] !is LiloGPUDim || values[1] !is LiloGPUDim) {
            throw createLiloException(liloTypeErrorType, "Kernal config expected (blocks: gpu.Dim, threads: gpu.Dim)")
        }

        val kernal = args[0] as LiloKernal
        val launchConfig = LiloLaunchConfig(blocksDim = values[0] as LiloGPUDim, threadsDim = values[1] as LiloGPUDim)
        return LiloResult.Success(data = LiloConfiguredKernal(kernal.definition, launchConfig))
    }
}

private object KernalCall : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        throw createLiloException(liloTypeErrorType, "`gpu.Kernal` is not callable, use `gpu.launch` or `kernal[config]` to get callable kernal")
    }
}

data class LiloKernal(val definition: FunctionStmt) : LiloObject(liloKernalType) {
    override fun toString() = "<gpu.kernal '${definition.name}'>"
}

package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.compiler.LiloGPUCompiler
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloNone
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import kotlinx.coroutines.runBlocking

val liloConfiguredKernalType =
    LiloType(name = "gpu.ConfiguredKernal", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = LiloConfiguredKernalInit)
        it.setAttr(name = LiloMagicMethod.CALL, value = LiloConfiguredKernalCall)
    }

private object LiloConfiguredKernalInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if ((args.size != 2) || (args[0] !is LiloKernal) || (args[1] !is LiloLaunchConfig)) {
            throw createLiloException(liloTypeErrorType, "`gpu.Kernal` expects two argument (launch_config, kernal)")
        }

        val kernal = args[0] as LiloKernal
        val launchConfig = args[1] as LiloLaunchConfig
        return LiloResult.Success(data = LiloConfiguredKernal(definition = kernal.definition, launchConfig))
    }
}

private object LiloConfiguredKernalCall : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> = runBlocking {
        val gpuDevice = interpreter.liloMachine.getGPU()!!
        val self = args[0] as LiloConfiguredKernal
        val kernalArgs = args.drop(n = 1)
        val gpuCompiler = LiloGPUCompiler(self.config)
        val gpuCode = gpuCompiler.visit(stmt = self.definition).valueOr { return@runBlocking it.toFailure() }
        gpuDevice.launchKernal(gpuCode, kernal = self, kernalArgs)
        return@runBlocking LiloResult.Success(data = LiloNone)
    }
}

data class LiloConfiguredKernal(
    val definition: FunctionStmt,
    val config: LiloLaunchConfig
) : LiloObject(liloConfiguredKernalType) {
    override fun toString() = "<gpu.ConfiguredKernal '${definition.name}'>"
}

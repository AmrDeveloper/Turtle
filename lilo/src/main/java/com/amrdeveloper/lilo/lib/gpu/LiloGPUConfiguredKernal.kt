package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloConfiguredKernalType =
    LiloType(name = "gpu.ConfiguredKernal", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = LiloConfiguredKernalInit)
    }

private object LiloConfiguredKernalInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if ((args.size != 2) || (args[0] !is LiloKernal) || (args[1] !is LiloLaunchConfig)) {
            return LiloResult.Failure(error = RuntimeException("`gpu.Kernal` expects two argument (launch_config, kernal)"))
        }

        val kernal = args[0] as LiloKernal
        val launchConfig = args[1] as LiloLaunchConfig
        return LiloResult.Success(data = LiloConfiguredKernal(definition = kernal.definition, launchConfig))
    }
}

data class LiloConfiguredKernal(
    val definition: FunctionStmt,
    val config: LiloLaunchConfig
) : LiloObject(liloConfiguredKernalType) {
    override fun toString() = "<gpu.ConfiguredKernal '${definition.name}'>"
}

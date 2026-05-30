package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.ast.FunctionStmt
import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloFunction
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloKernalType =
    LiloType(name = "gpu.kernal", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = KernalInit)
        it.setAttr(name = LiloMagicMethod.CALL, value = KernalCall)
    }

private object KernalInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1 || args[0] !is LiloFunction) {
            return LiloResult.Failure(error = RuntimeException("`gpu.Kernal` expects one argument which is function"))
        }
        val function = args[0] as LiloFunction
        return LiloResult.Success(data = LiloKernal(definition = function.definition))
    }
}

private object KernalCall : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        return LiloResult.Failure(error = RuntimeException("`gpu.Kernal` is not callable, use `gpu.launch` or `kernal[config]` to get callable kernal"))
    }
}

data class LiloKernal(val definition: FunctionStmt) : LiloObject(liloKernalType) {
    override fun toString() = "<gpu.kernal '${definition.name}'>"
}

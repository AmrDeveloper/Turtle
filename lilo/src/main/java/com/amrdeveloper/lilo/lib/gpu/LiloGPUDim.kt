package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.machine.device.LiloConfigDim3
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.liloFunctionType

val liloDimType = LiloType(name = "gpu.Dim", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE))
    .also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = GPUDimInit)
        it.setAttr(name = LiloMagicMethod.STR, value = GPUDimStr)
    }

private object GPUDimInit : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size !in 1..3) {
            return LiloResult.Failure(error = LiloExceptionMessage("Dim expects 1 to 3 args not ${args.size}"))
        }

        var x = 0
        var y = 0
        var z = 0
        for ((index, arg) in args.withIndex()) {
            if (arg !is LiloInt)
                return LiloResult.Failure(error = LiloExceptionMessage("Dim arg $index expected to be Int but got ${arg.type}"))
            when (index) {
                0 -> x = arg.value
                1 -> y = arg.value
                2 -> z = arg.value
            }
        }
        return LiloResult.Success(data = LiloGPUDim(LiloConfigDim3(x, y, z)))
    }
}

private object GPUDimStr : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val dim = args[0] as LiloGPUDim
        val dim3 = dim.dim
        val str = "(x=${dim3.x}, y=${dim3.y}, z=${dim3.z})"
        return LiloResult.Success(data = LiloStr(value = str))
    }
}

data class LiloGPUDim(
    val dim: LiloConfigDim3
) : LiloObject(liloDimType) {
    override fun toString() = "gpu.Dim(x=${dim.x}, y=${dim.y}, z=${dim.z})"
}

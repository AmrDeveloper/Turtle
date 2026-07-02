package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.machine.device.LiloConfigDim3
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType

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
            throw createLiloException(liloTypeErrorType, "Dim expects 1 to 3 args not ${args.size}")
        }

        var x = 1
        var y = 1
        var z = 1
        for ((index, arg) in args.withIndex()) {
            if (arg !is LiloInt)
                throw createLiloException(liloTypeErrorType, "Dim arg $index expected to be Int but got ${arg.type}")
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
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "Dim.str expects 1args not ${args.size}")
        }

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

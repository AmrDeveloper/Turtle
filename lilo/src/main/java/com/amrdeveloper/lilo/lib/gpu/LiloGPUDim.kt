package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.objects.LiloBaseType
import com.amrdeveloper.lilo.objects.LiloType
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloMethodType

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
        if (args.size !in 1 .. 3) {
            return LiloResult.Failure(error = LiloExceptionMessage("Dim expects 1 to 3 args not ${args.size}"))
        }

        val dim = LiloObject(type = liloDimType)
        for ((index, arg) in args.withIndex()) {
            if (arg !is LiloInt)
                return LiloResult.Failure(error = LiloExceptionMessage("Dim arg $index expected to be Int but got ${arg.type}"))
            when (index) {
                0 -> dim.setAttr(name = "x", value = arg)
                1 -> dim.setAttr(name = "y", value = arg)
                2 -> dim.setAttr(name = "z", value = arg)
            }
        }
        return LiloResult.Success(data = dim)
    }
}

private object GPUDimStr : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val dim3 = args[0]
        var xValue = 1
        val x = dim3.getAttr(name = "x")
        if (x != null && x is LiloInt) xValue = x.value

        var yValue = 1
        val y = dim3.getAttr(name = "y")
        if (y != null && y is LiloInt) yValue = y.value

        var zValue = 1
        val z = dim3.getAttr(name = "z")
        if (z != null && z is LiloInt) zValue = z.value

        val str = "(x=${xValue}, y=${yValue}, z=${zValue})"
        return LiloResult.Success(data = LiloStr(value = str))
    }
}

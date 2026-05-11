package com.amrdeveloper.lilo.std.modules.gpu

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloStr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.LiloBaseType
import com.amrdeveloper.lilo.type.LiloType
import com.amrdeveloper.lilo.type.liloMethodType

val liloDim3Type = LiloType(name = "Dim3", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE))
    .also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.INIT, value = Dim3Init)

        it.setAttr(name = LiloMagicMethod.STR, value = Dim3Str)
    }

private object Dim3Init : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size > 3)
            return LiloResult.Failure(error = LiloExceptionMessage("Dim3 expects 0 to 3 args not ${args.size}"))

        val dim3 = LiloObject(type = liloDim3Type)
        for ((index, arg) in args.withIndex()) {
            if (arg !is LiloInt)
                return LiloResult.Failure(error = LiloExceptionMessage("Dim3 arg ${index} expected to be Int"))
            when (index) {
                0 -> dim3.setAttr(name = "x", value = arg)
                1 -> dim3.setAttr(name = "y", value = arg)
                2 -> dim3.setAttr(name = "z", value = arg)
            }
        }
        return LiloResult.Success(data = dim3)
    }
}

private object Dim3Str : LiloObject(liloMethodType), LiloCallable {
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

        val str = "(x:${xValue}, y:${yValue}, z:${zValue})"
        return LiloResult.Success(data = LiloStr(value = str))
    }
}

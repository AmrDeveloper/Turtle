package com.amrdeveloper.lilo.std.builtins

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFunctionType

object LiloPrintFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val output = StringBuilder()
        for (arg in args) {
            val strMethod = arg.getAttr(name = LiloMagicMethod.STR)
            if (strMethod != null && strMethod is LiloCallable) {
                val invokeResult = strMethod.invoke(interpreter, args = listOf(arg))
                if (invokeResult.isFailure()) return invokeResult
                output.append(invokeResult.toSuccessData().toString())
                continue
            }
            output.append(arg.toString())
        }
        interpreter.liloMachine.getHost().write(output.toString())
        return LiloResult.Success<LiloObject>(data = LiloInt(value = 0))
    }
}

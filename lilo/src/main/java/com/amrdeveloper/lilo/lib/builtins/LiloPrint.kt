package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.str
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloPrintFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val output = StringBuilder()
        for (arg in args) {
            val stringVal = arg.str(interpreter).valueOr { return it.toFailure() }
            output.append(stringVal)
        }
        interpreter.liloMachine.getHost().write(output.toString())
        return LiloResult.Success<LiloObject>(data = LiloInt(value = 0))
    }
}

package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloHexFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val argument = args[0]
        if (argument !is LiloInt) {
            return LiloResult.Failure(error = LiloExceptionMessage(message = "Expect `Int` but got `${argument.type}`"))
        }
        return LiloResult.Success(data = LiloStr(value = "0x" + argument.value.toString(radix = 16)))
    }
}

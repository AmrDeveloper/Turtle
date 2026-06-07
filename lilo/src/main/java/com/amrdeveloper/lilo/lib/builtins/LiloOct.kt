package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloStr
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloOctFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1 || args[0] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`oct` Expect `Int` but got `${args[0].type}`")
        }
        val argument = args[0] as LiloInt
        return LiloResult.Success(data = LiloStr(value = "0o" + argument.value.toString(radix = 8)))
    }
}

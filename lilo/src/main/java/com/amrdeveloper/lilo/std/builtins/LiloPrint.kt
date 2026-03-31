package com.amrdeveloper.lilo.std.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.value.LiloCallable
import com.amrdeveloper.lilo.value.LiloInt
import com.amrdeveloper.lilo.value.LiloValue

class LiloPrintFunction : LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloValue>
    ): LiloResult<LiloValue> {
        val output = args.joinToString(separator = " ")
        interpreter.liloHost.write(output)
        return LiloResult.Success<LiloValue>(data = LiloInt(value = 0))
    }
}
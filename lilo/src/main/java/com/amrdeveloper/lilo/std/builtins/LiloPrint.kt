package com.amrdeveloper.lilo.std.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloHost
import com.amrdeveloper.lilo.std.core.LiloStdFunction
import com.amrdeveloper.lilo.value.LiloInt
import com.amrdeveloper.lilo.value.LiloValue

class LiloPrintFunction : LiloStdFunction {
    override fun call(
        host: LiloHost,
        args: List<LiloValue>
    ): LiloResult<LiloValue> {
        val output = args.joinToString(separator = " ")
        host.write(output)
        return LiloResult.Success<LiloValue>(data = LiloInt(value = 0))
    }
}
package com.amrdeveloper.lilo.std.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloObject

class LiloPrintFunction : LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val output = args.joinToString(separator = " ")
        interpreter.liloHost.write(output)
        return LiloResult.Success<LiloObject>(data = LiloInt(value = 0))
    }
}
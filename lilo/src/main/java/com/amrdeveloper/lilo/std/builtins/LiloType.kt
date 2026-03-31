package com.amrdeveloper.lilo.std.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloStr
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloTypeFunction : LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val output = LiloStr(value = args[0].type.toString())
        return LiloResult.Success<LiloObject>(data = output)
    }
}
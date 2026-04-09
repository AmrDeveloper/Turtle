package com.amrdeveloper.lilo.std.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloBool
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFunctionType

object LiloHasAttrFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val obj = args[0]
        val attrName = args[1].toString()
        return LiloResult.Success(data = LiloBool(value = obj.hasAttr(attrName)))
    }
}

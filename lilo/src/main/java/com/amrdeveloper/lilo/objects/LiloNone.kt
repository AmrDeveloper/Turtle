package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloNoneType = LiloType(name = "NoneType", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.BOOL, value = NoneBool)
}

private object NoneBool : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        return LiloResult.Success(data = LiloBool(value = false))
    }
}

object LiloNone : LiloObject(liloNoneType) {
    override fun toString() = "None"
}

package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloBoolType = LiloType(name = "bool", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

data class LiloBool(val value: Boolean) : LiloObject(liloBoolType) {

    init {
        setAttr(name = LiloMagicMethod.BOOL, value = BoolBool)
    }

    override fun toString() = if (value) "True" else "False"
}

private object BoolBool : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloBool
        return LiloResult.Success(data = self)
    }
}

package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloBoolType
import com.amrdeveloper.lilo.type.liloMethodType

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

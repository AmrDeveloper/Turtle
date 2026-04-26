package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloIntType
import com.amrdeveloper.lilo.type.liloMethodType

data class LiloInt(val value: Int) : LiloObject(liloIntType) {

    init {
        setAttr(name = LiloMagicMethod.BOOL, value = IntBool)
    }

    override fun toString() = value.toString()
}

private object IntBool : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloInt
        return LiloResult.Success(data = LiloBool(value = self.value != 0))
    }
}

package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFloatType
import com.amrdeveloper.lilo.type.liloMethodType

data class LiloFloat(val value: Float) : LiloObject(liloFloatType) {

    init {
        setAttr(name = LiloMagicMethod.BOOL, value = FloatBool)
    }

    override fun toString(): String {
        if (value == Float.POSITIVE_INFINITY) return "inf"
        if (value.isNaN()) return "nan"
        return value.toString()
    }
}

private object FloatBool : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloFloat
        return LiloResult.Success(data = LiloBool(value = self.value != 0.0f))
    }
}

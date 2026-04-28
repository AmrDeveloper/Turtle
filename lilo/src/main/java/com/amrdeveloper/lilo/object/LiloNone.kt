package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloMethodType
import com.amrdeveloper.lilo.type.liloNoneType

data class LiloNone(private val unit: Unit = Unit) : LiloObject(liloNoneType) {
    init {
        setAttr(name = LiloMagicMethod.BOOL, value = NoneBool)
    }
    override fun toString() = "None"
}

private object NoneBool : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        return LiloResult.Success(data = LiloBool(value = false))
    }
}

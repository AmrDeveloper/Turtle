package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloStr
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloStrType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'str'>"

    init {
        define(name = LiloMagicMethod.STR, value = StrMethod)
    }

    private object StrMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0] as LiloStr
            return LiloResult.Success(data = LiloStr(value = lhs.value))
        }
    }
}
package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloSetType = LiloType(name = "set", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.LEN, value = SetLen)
}

private object SetLen : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0]
        if (self !is LiloSet) return LiloResult.Failure(error = LiloExceptionMessage("Expected type to be List"))
        return LiloResult.Success(data = LiloInt(value = self.values.size))
    }
}
class LiloSet(val values: MutableSet<LiloObject>) : LiloObject(liloSetType) {
    override fun toString(): String {
        return "{".plus(values.joinToString(", ") { it.toString() }).plus("}")
    }
}

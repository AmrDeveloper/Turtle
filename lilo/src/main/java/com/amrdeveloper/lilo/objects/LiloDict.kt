package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloDictType = LiloType(name = "dict", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.LEN, value = DictLen)
}

private object DictLen : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0]
        if (self !is LiloDict) return LiloResult.Failure(error = LiloExceptionMessage("Expected type to be Dict"))
        return LiloResult.Success(data = LiloInt(value = self.values.size))
    }
}

class LiloDict(val values: MutableMap<LiloObject, LiloObject>) : LiloObject(liloDictType) {
    override fun toString(): String {
        val buffer = StringBuilder()
        buffer.append("{")
        var i = 0
        for ((key, value) in values) {
            buffer.append("$key:$value")
            if (i != values.size - 1) buffer.append(", ")
            i++
        }
        buffer.append("}")
        return buffer.toString()
    }
}

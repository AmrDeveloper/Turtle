package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
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
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`dict.__init__` Expect 1 arguments but got `${args.size}`")
        }

        if (args[0] !is LiloDict) {
            throw createLiloException(liloTypeErrorType, "`dict.__init__` Expect 1 arguments dict but got `${args[0].type}`")
        }

        val self = args[0] as LiloDict
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

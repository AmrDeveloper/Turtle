package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloTupleType = LiloType(name = "tuple", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.GET_ITEM, value = TupleGetItem)
    it.setAttr(name = LiloMagicMethod.LEN, value = TupleLen)
}

private object TupleGetItem : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`tuple.__getitem__` Expect 2 arguments  but got `${args.size}`")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "Tuple[i] index must be int, got ${args[1].type}")
        }

        val lhs = args[0]
        val index = args[1] as LiloInt
        val list = lhs as LiloTuple
        val item = list.values[index.value]
        return LiloResult.Success(data = item)
    }
}

private object TupleLen : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`tuple.__len__` Expect 2 arguments  but got `${args.size}`")
        }

        if (args[0] !is LiloTuple) {
            throw createLiloException(liloTypeErrorType, "tuple.__len__ expected type to be List, got ${args[0].type}")
        }

        val self = args[0] as LiloTuple
        return LiloResult.Success(data = LiloInt(value = self.values.size))
    }
}

data class LiloTuple(val values: List<LiloObject>) : LiloObject(liloTupleType) {
    override fun toString(): String {
        return "(".plus(values.joinToString(", ") { it.toString() }).plus(")")
    }
}

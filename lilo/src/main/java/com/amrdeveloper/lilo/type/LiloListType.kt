package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloList
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloListType = LiloType(name = "list", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.GET_ITEM, value = ListGetItem)

    it.setAttr(name = LiloMagicMethod.LEN, value = ListLen)
}

private object ListGetItem : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val lhs = args[0]
        val index = args[1]
        if (index !is LiloInt) return LiloResult.Failure(error = LiloException("List index must be int"))
        val list = lhs as LiloList
        val item = list.values[index.value]
        return LiloResult.Success(data = item)
    }
}

private object ListLen : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0]
        if (self !is LiloList) return LiloResult.Failure(error = LiloException("Expected type to be List"))
        return LiloResult.Success(data = LiloInt(value = self.values.size))
    }
}

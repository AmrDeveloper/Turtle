package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloList
import com.amrdeveloper.lilo.`object`.LiloNone
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloListType = LiloType(name = "list", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.SET_ITEM, value = ListSetItem)
    it.setAttr(name = LiloMagicMethod.GET_ITEM, value = ListGetItem)

    it.setAttr(name = LiloMagicMethod.LEN, value = ListLen)

    it.setAttr(name = "append", value = ListAppend)
    it.setAttr(name = "extend", value = ListExtend)
}

private object ListAppend : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0]
        val index = args[1]
        val list = self as LiloList
        list.values.add(index)
        return LiloResult.Success(data = LiloNone)
    }
}

private object ListExtend : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0]
        val other = args[1]
        val list = self as LiloList
        if (other !is LiloList) return LiloResult.Failure(error = LiloException("invalid parameter for `list.extend`"))
        list.values.addAll(other.values)
        return LiloResult.Success(data = LiloNone)
    }
}

private object ListSetItem : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val index = args[1]
        if (index !is LiloInt) return LiloResult.Failure(error = LiloException("List key must be int"))
        val self = args[0] as LiloList
        val value = args[2]
        self.values[index.value] = value
        return LiloResult.Success(data = LiloNone)
    }
}


private object ListGetItem : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val index = args[1]
        if (index !is LiloInt) return LiloResult.Failure(error = LiloException("List index must be int"))
        val self = args[0] as LiloList
        val item = self.values[index.value]
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

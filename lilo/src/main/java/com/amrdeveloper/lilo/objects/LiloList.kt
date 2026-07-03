package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloListType = LiloType(name = "list", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.MUL, value = ListMul)

    it.setAttr(name = LiloMagicMethod.SET_ITEM, value = ListSetItem)
    it.setAttr(name = LiloMagicMethod.GET_ITEM, value = ListGetItem)
    it.setAttr(name = LiloMagicMethod.LEN, value = ListLen)

    it.setAttr(name = "append", value = ListAppend)
    it.setAttr(name = "extend", value = ListExtend)
}

private object ListAppend : LiloObject(liloFunctionType), LiloCallable {
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

private object ListExtend : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`list.extend` Expect 2 arguments but got `${args.size}`")
        }

        if (args[0] !is LiloList) {
            throw createLiloException(liloTypeErrorType, "`list.extend` Expect first argument to be list, got ${args[0].type}")
        }

        if (args[1] !is LiloList) {
            throw createLiloException(liloTypeErrorType, "`list.extend` Expect second argument to be list, got ${args[1].type}")
        }

        val self = args[0] as LiloList
        val other = args[1] as LiloList
        self.values.addAll(elements = other.values)
        return LiloResult.Success(data = LiloNone)
    }
}

private object ListMul : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`list.__mul__` Expect 2 arguments got ${args.size}")
        }

        if (args[0] !is LiloList) {
            throw createLiloException(liloTypeErrorType, "`list.__mul__` Expect first argument to be list, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`list.__mul__` Expect second argument to be int, got ${args[1].type}")
        }

        val self = args[0] as LiloList
        val times = args[1] as LiloInt
        val newList = buildList {
            repeat(times = times.value) {
                addAll(elements = self.values)
            }
        }
        return LiloResult.Success(data = LiloList(values = newList.toMutableList()))
    }
}

private object ListSetItem : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 3) {
            throw createLiloException(liloTypeErrorType, "`list.__setitem__` Expect 3 arguments got ${args.size}")
        }

        if (args[0] !is LiloList) {
            throw createLiloException(liloTypeErrorType, "`list.__setitem__` Expect first argument to be list, got ${args[0].type}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`list.__setitem__` Expect second argument to be int, got ${args[1].type}")
        }

        val self = args[0] as LiloList
        val index = args[1] as LiloInt
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
        if (args.size != 2) {
            throw createLiloException(liloTypeErrorType, "`list.__getitem__` Expect 2 arguments got ${args.size}")
        }

        if (args[1] !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "`list.__getitem__` Expect second argument to be int, got ${args[1].type}")
        }

        val self = args[0] as LiloList
        val index = args[1] as LiloInt
        val item = self.values[index.value]
        return LiloResult.Success(data = item)
    }
}

private object ListLen : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "`list.__len__` Expect 1 arguments got ${args.size}")
        }

        if (args[0] !is LiloList) {
            throw createLiloException(liloTypeErrorType, "`list.__len__` Expect first argument to be list, got ${args[0].type}")
        }

        val self = args[0] as LiloList
        return LiloResult.Success(data = LiloInt(value = self.values.size))
    }
}


data class LiloList(val values: MutableList<LiloObject>) : LiloObject(liloListType) {
    override fun toString(): String {
        return "[".plus(values.joinToString(", ") { it.toString() }).plus("]")
    }
}

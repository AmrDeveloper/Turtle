package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.runtime.LiloRaise

val liloTupleType = LiloType(name = "tuple", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = "index", value = TupleIndex)

    it.setAttr(name = LiloMagicMethod.GET_ITEM, value = TupleGetItem)
    it.setAttr(name = LiloMagicMethod.LEN, value = TupleLen)

    it.setAttr(name = LiloMagicMethod.ITER, value = TupleIter)
    it.setAttr(name = LiloMagicMethod.REVERSED, value = TupleReversedIter)
}

private object TupleIndex : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size !in 2..4) {
            throw createLiloException(liloTypeErrorType, "`tuple.index` Expect 1 to 3 arguments but got `${args.size}`")
        }

        if (args[0] !is LiloTuple) {
            throw createLiloException(liloTypeErrorType, "`tuple.index` expected first argument to be Tuple, got ${args[0].type}")
        }

        val self = args[0] as LiloTuple
        val count = self.values.size

        val target = args[1]

        var start = 0
        if (args.size > 2) {
            if (args[2] !is LiloInt)
                throw createLiloException(liloTypeErrorType, "`tuple.index` expected second argument to be int, got ${args[2].type}")
            start = (args[2] as LiloInt).value
        }

        var end = count
        if (args.size > 3) {
            if (args[3] !is LiloInt)
                throw createLiloException(liloTypeErrorType, "`tuple.index` expected third argument to be int, got ${args[3].type}")
            end = (args[3] as LiloInt).value
        }

        for (index in start until end) {
            if (index in self.values.indices) {
                val areEquals = self.values[index].eq(interpreter, other = target).valueOr { return it.toFailure() }
                if (areEquals) return LiloResult.Success(data = LiloInt(value = index))
            }
        }

        throw createLiloException(liloValueErrorType, "tuple.index(x): x not in tuple")
    }
}

private object TupleIter : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "tuple.__iter__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloTuple) {
            throw createLiloException(liloTypeErrorType, "tuple.__iter__ expected 1 arguments `tuple` but got ${args[0].type}")
        }

        val self = args[0] as LiloTuple
        return LiloResult.Success(data = LiloTupleIter(values = self.values, index = 0, isReversed = false))
    }
}

private object TupleReversedIter : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "tuple.__reversed__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloTuple) {
            throw createLiloException(liloTypeErrorType, "tuple.__reversed__ expected 1 arguments `tuple` but got ${args[0].type}")
        }

        val self = args[0] as LiloTuple
        return LiloResult.Success(data = LiloTupleIter(values = self.values, index = self.values.lastIndex, isReversed = true))
    }
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

val liloTupleIterType =
    LiloType(name = "tuple_iterator", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.ITER, value = TupleIterIter)
        it.setAttr(name = LiloMagicMethod.NEXT, value = TupleIterNext)
    }

private object TupleIterIter : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "tuple_iterator.__iter__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloTupleIter) {
            throw createLiloException(liloTypeErrorType, "tuple_iterator.__iter__ expected 1 arguments `range_iterator` but got ${args[0].type}")
        }

        return LiloResult.Success(data = args[0])
    }
}

private object TupleIterNext : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "tuple_iterator.__next__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloTupleIter) {
            throw createLiloException(liloTypeErrorType, "tuple_iterator.__next__ expected 1 arguments `range_iterator` but got ${args[0].type}")
        }

        val self = args[0] as LiloTupleIter
        if (self.isReversed) {
            if (self.index >= 0) {
                val result = self.values[self.index]
                self.index--
                return LiloResult.Success(data = result)
            }
        } else {
            if (self.index < self.values.size) {
                val result = self.values[self.index]
                self.index++
                return LiloResult.Success(data = result)
            }
        }
        throw LiloRaise(exception = liloStopIterationType)
    }
}

data class LiloTupleIter (
    val values : List<LiloObject>,
    var index : Int = 0,
    val isReversed: Boolean = false,
) : LiloObject(liloTupleIterType)

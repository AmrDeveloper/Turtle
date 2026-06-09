package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.runtime.LiloRaise

val liloRangeIterType =
    LiloType(name = "range_iterator", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE

        it.setAttr(name = LiloMagicMethod.ITER, value = RangeIterIter)
        it.setAttr(name = LiloMagicMethod.NEXT, value = RangeIterNext)
    }

private object RangeIterIter : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "range_iterator.__iter__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloRangeIter) {
            throw createLiloException(liloTypeErrorType, "range_iterator.__iter__ expected 1 arguments `range_iterator` but got ${args[0].type}")
        }

        return LiloResult.Success(data = args[0])
    }
}

private object RangeIterNext : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "range_iterator.__next__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloRangeIter) {
            throw createLiloException(liloTypeErrorType, "range_iterator.__next__ expected 1 arguments `range_iterator` but got ${args[0].type}")
        }

        val self = args[0] as LiloRangeIter
        if (self.len > 0) {
            val result = self.start
            self.start = result + self.step
            self.len--
            return LiloResult.Success(data = LiloInt(value = result))
        }
        throw LiloRaise(exception = liloStopIterationType)
    }
}

data class LiloRangeIter(
    var start: Int,
    val stop: Int,
    val step: Int,
    var len: Int,
) : LiloObject(liloRangeIterType)

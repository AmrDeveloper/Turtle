package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloRangeType = LiloType(name = "range", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.ITER, value = RangeIter)
    it.setAttr(name = LiloMagicMethod.REVERSED, value = RangeReversedIter)
}

private object RangeIter : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "range.__iter__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloRange) {
            throw createLiloException(liloTypeErrorType, "range.__iter__ expected 1 arguments `range` but got ${args[0].type}")
        }

        val self = args[0] as LiloRange
        val len = (self.stop - self.start) / self.step
        return LiloResult.Success(data = LiloRangeIter(self.start, self.stop, self.step, len))
    }
}

private object RangeReversedIter : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "range.__reversed__ expected 1 arguments but got ${args.size}")
        }

        if (args[0] !is LiloRange) {
            throw createLiloException(liloTypeErrorType, "range.__reversed__ expected 1 arguments `range` but got ${args[0].type}")
        }

        // From CPython 3.15
        //   reversed(range(start, stop, step)) can be expressed as
        //   range(start+(n-1)*step, start-step, -step), where n is the number of
        //   integers in the range.
        //
        val self = args[0] as LiloRange
        val n = (self.stop - self.start) / self.step
        val start = self.start + (n - 1) * self.step
        val stop = self.start - self.step
        val step = -self.step
        return LiloResult.Success(data = LiloRangeIter(start, stop, step, len = n))
    }
}

data class LiloRange(
    val start: Int = 0,
    val stop: Int,
    val step: Int = 1
) : LiloObject(liloRangeType) {
    override fun toString() = "range($start, $stop, $step)"
}

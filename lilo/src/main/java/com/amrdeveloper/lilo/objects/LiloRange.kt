package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloRangeType = LiloType(name = "range", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.ITER, value = RangeIter)
}

private object RangeIter : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloRange
        val len = (self.stop - self.start) / self.step
        val rangeIter = LiloRangeIter(self.start, self.stop, self.step, len)
        return LiloResult.Success(data = rangeIter)
    }
}

data class LiloRange(
    val start: Int = 0,
    val stop: Int,
    val step: Int = 1
) : LiloObject(liloRangeType) {
    override fun toString() = "range($start, $stop, $step)"
}

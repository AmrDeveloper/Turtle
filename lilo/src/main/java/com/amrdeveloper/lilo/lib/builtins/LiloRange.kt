package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloInt
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.LiloRange
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloRangeFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size > 3) {
            throw createLiloException(liloTypeErrorType, "range expected at most 3 arguments, got ${args.size}")
        }

        var start = 0
        var stop = 0
        var step = 1
        if (args.size == 1) {
            if (args[0] !is LiloInt) {
                throw createLiloException(liloTypeErrorType, "range arg 0 object cannot be interpreted as an integer")
            }
            stop = (args[0] as LiloInt).value
        } else {
            for ((index, arg) in args.iterator().withIndex()) {
                if (arg !is LiloInt) {
                    throw createLiloException(liloTypeErrorType, "range arg $index object cannot be interpreted as an integer")
                }

                when (index) {
                    0 -> start = arg.value
                    1 -> stop = arg.value
                    2 -> step = arg.value
                    else -> {}
                }
            }
        }
        return LiloResult.Success(data = LiloRange(start, stop, step))
    }
}

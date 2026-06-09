package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloReversedFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "reversed expected at least 1 argument, got ${args.size}")
        }

        val argument = args[0]
        val iterator = argument.getAttr(name = LiloMagicMethod.REVERSED)
        if (iterator == null || iterator !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "'${argument.type}' object is not reversed iterable")
        }

        val actualIterator = iterator.invoke(interpreter, listOf(argument)).valueOr { return it.toFailure() }
        if (!actualIterator.hasAttr(name = LiloMagicMethod.NEXT)) {
            throw createLiloException(liloTypeErrorType, "reversed() returned non-iterator of type '${iterator.type}'")
        }

        return LiloResult.Success(data = actualIterator)
    }
}

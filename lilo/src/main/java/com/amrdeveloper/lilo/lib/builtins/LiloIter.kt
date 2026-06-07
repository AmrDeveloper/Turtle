package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloIterFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "iter expected at least 1 argument, got ${args.size}")
        }

        val argument = args[0]
        val iterator = argument.getAttr(name = LiloMagicMethod.ITER)
        if (iterator == null || iterator !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "'${argument.type}' object is not iterable")
        }

        if (!iterator.hasAttr(name = LiloMagicMethod.NEXT)) {
            throw createLiloException(liloTypeErrorType, "iter() returned non-iterator of type '${argument.type}'")
        }

        return LiloResult.Success(data = iterator)
    }
}

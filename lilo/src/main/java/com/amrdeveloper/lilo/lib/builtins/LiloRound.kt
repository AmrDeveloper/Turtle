package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloRoundFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            throw createLiloException(liloTypeErrorType, "round expected at least 1 argument, got ${args.size}")
        }

        val argument = args[0]
        val magicRoundFunction = argument.getAttr(name = LiloMagicMethod.ROUND)
        if ((magicRoundFunction == null) || magicRoundFunction !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "`${argument}` doesn't support __round__")
        }

        val roundResult = magicRoundFunction.invoke(interpreter, args = listOf(argument))
        if (roundResult.isFailure()) {
            throw createLiloException(liloTypeErrorType, "round function failed with input `${argument}")
        }
        val round = roundResult.toSuccessData()
        return LiloResult.Success(data = round)
    }
}

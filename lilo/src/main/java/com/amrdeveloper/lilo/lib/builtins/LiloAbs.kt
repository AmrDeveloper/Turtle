package com.amrdeveloper.lilo.lib.builtins

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.createLiloException
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloTypeErrorType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloAbsFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) throw createLiloException(liloTypeErrorType, " abs() takes exactly one argument (${args.size} given)")
        val argument = args[0]
        val abs = argument.getAttr(name = LiloMagicMethod.ABS)
        if (abs == null || abs !is LiloCallable) throw createLiloException(liloTypeErrorType, "bad operand type for abs(): '${args[0].type}'")
        return abs.invoke(interpreter, args)
    }
}

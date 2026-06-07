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

object LiloLenFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val collection = args[0]
        val magicLenFunction = collection.getAttr(name = LiloMagicMethod.LEN)
        if ((magicLenFunction == null) || magicLenFunction !is LiloCallable) {
            throw createLiloException(liloTypeErrorType, "`${collection}` doesn't support __len__")
        }

        val lengthResult = magicLenFunction.invoke(interpreter, args = listOf(collection))
        if (lengthResult.isFailure()) {
            throw createLiloException(liloTypeErrorType, "Len function failed with input `${collection}")
        }
        val len = lengthResult.toSuccessData()
        return LiloResult.Success(data = len)
    }
}

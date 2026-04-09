package com.amrdeveloper.lilo.std.builtins

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toSuccessData
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloStr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFunctionType

object LiloLenFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val collection = args[0]
        val magicLenFunction = collection.getAttr(name = LiloMagicMethod.LEN)
        if ((magicLenFunction == null) || magicLenFunction !is LiloCallable) {
            return LiloResult.Failure(error = LiloException(message = "`${collection}` doesn't support __len__"))
        }

        val lengthResult = magicLenFunction.invoke(interpreter, args = listOf(collection))
        if (lengthResult.isFailure()) {
            return LiloResult.Failure<LiloObject>(error = LiloStr(value = "Len function failed with input `${collection}"))
        }
        val len = lengthResult.toSuccessData()
        return LiloResult.Success(data = len)
    }
}

package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloFloatType = LiloType(name = "float", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    // Unary
    it.setAttr(name = LiloMagicMethod.POS, value = FloatPos)
    it.setAttr(name = LiloMagicMethod.NEG, value = FloatNeg)
}

private object FloatPos : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val operand = args[0]
        if (operand is LiloFloat) {
            return LiloResult.Success(data = operand)
        }
        return LiloResult.Failure(error = LiloExceptionMessage("descriptor '__pos__' requires a 'float' object but received a '${operand.type}'"))
    }
}

private object FloatNeg : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val operand = args[0]
        if (operand is LiloFloat) {
            return LiloResult.Success(data = LiloFloat(value = -operand.value))
        }
        return LiloResult.Failure(error = LiloExceptionMessage("descriptor '__neg__' requires a 'float' object but received a '${operand.type}'"))
    }
}

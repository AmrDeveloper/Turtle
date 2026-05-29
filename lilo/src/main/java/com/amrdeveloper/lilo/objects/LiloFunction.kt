package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.ast.LiloStmt
import com.amrdeveloper.lilo.ast.Parameter
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloEnvironment
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.runtime.signal.LiloReturnSignal

val liloFunctionType =
    LiloType(name = "function", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }


data class LiloFunction(val params: List<Parameter>, val body: LiloStmt) :
    LiloObject(liloFunctionType), LiloCallable {

    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val previous = interpreter.environment
        interpreter.environment = LiloEnvironment(enclosing = interpreter.environment)

        for ((index, arg) in args.withIndex()) {
            interpreter.environment.set(name = params[index].name, value = arg)
        }

        try {
            interpreter.visit(stmt = body).valueOr {
                interpreter.environment = previous
                return it.toFailure()
            }
        } catch (retSignal: LiloReturnSignal) {
            if (retSignal.value != null) {
                interpreter.environment = previous
                return LiloResult.Success(data = retSignal.value)
            }
        }

        interpreter.environment = previous
        return LiloResult.Success(data = LiloNone)
    }
}

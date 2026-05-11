package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.ast.LiloStmt
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.runtime.signal.LiloReturnSignal
import com.amrdeveloper.lilo.type.liloFunctionType

data class LiloFunction(val params: List<String>, val body: List<LiloStmt>) :
    LiloObject(liloFunctionType), LiloCallable {

    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        for ((index, arg) in args.withIndex()) {
            interpreter.environment.set(name = params[index], value = arg)
        }

        try {
            for (stmt in body) {
                val result = interpreter.visit(stmt)
                if (result.isFailure()) return result.toFailure()
            }
        } catch (retSignal: LiloReturnSignal) {
            if (retSignal.value != null) return LiloResult.Success(data = retSignal.value)
            return LiloResult.Success(data = LiloNone)
        }

        return LiloResult.Success(data = LiloNone)
    }
}

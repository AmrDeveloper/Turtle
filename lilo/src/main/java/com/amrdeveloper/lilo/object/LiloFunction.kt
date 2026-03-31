package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.ast.LiloStmt
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.isFailure
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.runtime.LiloInterpreter

class LiloFunction(val params: List<String>, val body: List<LiloStmt>) : LiloCallable {

    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        for ((index, arg) in args.withIndex()) {
            interpreter.environment.define(name = params[index], value = arg)
        }

        for (stmt in body) {
            val result = interpreter.visit(stmt)
            if (result.isFailure()) return result.toFailure()
        }

        return LiloResult.Success(data = LiloInt(value = 0))
    }
}
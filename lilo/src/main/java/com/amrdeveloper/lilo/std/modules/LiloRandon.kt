package com.amrdeveloper.lilo.std.modules

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloModule
import com.amrdeveloper.lilo.`object`.LiloObject

import kotlin.random.Random

val liloRandomModule = LiloModule(name = "random").apply {
    type.define(name = "random", value = LiloRandomFunction())
}

class LiloRandomFunction : LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val random = Random.nextFloat()
        return LiloResult.Success(data = LiloFloat(value = random))
    }
}
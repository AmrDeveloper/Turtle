package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.value.LiloFloat
import com.amrdeveloper.lilo.value.LiloValue

import kotlin.random.Random

class LiloRandomModule : LiloStdModule {

    private val functions: Map<String, LiloStdFunction> = mapOf(
        "random" to LiloRandomFunction()
    )

    override fun getStdFunction(name: String): LiloStdFunction? {
        return functions.get(name)
    }
}

class LiloRandomFunction : LiloStdFunction {
    override fun call(args: List<LiloValue>): LiloResult<LiloValue> {
        val random = Random.nextFloat()
        return LiloResult.Success(data = LiloFloat(value = random))
    }
}
package com.amrdeveloper.lilo.std.modules

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.std.core.LiloStdModule
import com.amrdeveloper.lilo.value.LiloCallable
import com.amrdeveloper.lilo.value.LiloFloat
import com.amrdeveloper.lilo.value.LiloValue

import kotlin.random.Random

class LiloRandomModule : LiloStdModule {

    private val attributes: Map<String, LiloValue> = mapOf(
        "random" to LiloRandomFunction()
    )

    override fun lookup(name: String): LiloValue? {
        return attributes.get(name)
    }
}

class LiloRandomFunction : LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloValue>
    ): LiloResult<LiloValue> {
        val random = Random.nextFloat()
        return LiloResult.Success(data = LiloFloat(value = random))
    }
}
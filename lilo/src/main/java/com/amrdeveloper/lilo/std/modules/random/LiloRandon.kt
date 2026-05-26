package com.amrdeveloper.lilo.std.modules.random

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloFloat
import com.amrdeveloper.lilo.`object`.LiloList
import com.amrdeveloper.lilo.`object`.LiloModule
import com.amrdeveloper.lilo.`object`.LiloNone
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.liloFunctionType
import kotlin.random.Random

private const val MODULE_NAME = "random"

val liloRandomModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "random", value = LiloRandom)
    it.setAttr(name = "shuffle", value = LiloShuffle)
}

object LiloRandom : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val random = Random.nextDouble()
        return LiloResult.Success(data = LiloFloat(value = random))
    }
}

object LiloShuffle : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args[0] !is LiloList) {
            return LiloResult.Failure(error = RuntimeException("`Shuffle` expect list but got `${args[0].type.toString()}`"))
        }
        val list = args[0] as LiloList
        list.values.shuffle()
        return LiloResult.Success(data = LiloNone)
    }
}

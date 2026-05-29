package com.amrdeveloper.lilo.lib.random

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloFloat
import com.amrdeveloper.lilo.objects.LiloList
import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.LiloNone
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.objects.liloFunctionType
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

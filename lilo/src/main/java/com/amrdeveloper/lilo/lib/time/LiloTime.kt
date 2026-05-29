package com.amrdeveloper.lilo.lib.time

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloFloat
import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.objects.liloFunctionType

private const val MODULE_NAME = "time"

val liloTimeModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "time", value = LiloTime)
}

object LiloTime : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val currentTime = System.currentTimeMillis() / 1000.0
        return LiloResult.Success(data = LiloFloat(value = currentTime))
    }
}

package com.amrdeveloper.lilo.lib.inspect

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloBool
import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.LiloObject
import com.amrdeveloper.lilo.objects.liloFunctionType
import com.amrdeveloper.lilo.objects.liloMethodType
import com.amrdeveloper.lilo.objects.liloModuleType
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

private const val MODULE_NAME = "inspect"

val liloInspectModule = LiloModule(name = MODULE_NAME).also {
    // Functions
    it.setAttr(name = "ismodule", value = LiloIsModule)
    it.setAttr(name = "ismethod", value = LiloIsMethod)
    it.setAttr(name = "isfunction", value = LiloIsFunction)
}

object LiloIsModule : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            return LiloResult.Failure(error = RuntimeException("`inspect.ismodule` one argument"))
        }
        val isModule = args[0].type == liloModuleType
        return LiloResult.Success(data = LiloBool(value = isModule))
    }
}

object LiloIsMethod : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            return LiloResult.Failure(error = RuntimeException("`inspect.ismethod` one argument"))
        }
        val isModule = args[0].type == liloMethodType
        return LiloResult.Success(data = LiloBool(value = isModule))
    }
}

object LiloIsFunction : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 1) {
            return LiloResult.Failure(error = RuntimeException("`inspect.isfunction` one argument"))
        }
        val isModule = args[0].type == liloFunctionType
        return LiloResult.Success(data = LiloBool(value = isModule))
    }
}

package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloBoolType = LiloType(name = "bool", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.BOOL, value = BoolBool)
    it.setAttr(name = LiloMagicMethod.NOT, value = BoolNot)
}

private object BoolBool : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloBool
        return LiloResult.Success(data = self)
    }
}

private object BoolNot : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloBool
        return LiloResult.Success(data = LiloBool(value = self.value.not()))
    }
}

data class LiloBool(val value: Boolean) : LiloObject(liloBoolType) {
    override fun toString() = if (value) "True" else "False"
}

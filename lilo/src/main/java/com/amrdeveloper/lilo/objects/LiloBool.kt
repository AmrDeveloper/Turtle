package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloExceptionMessage
import com.amrdeveloper.lilo.runtime.LiloInterpreter

val liloBoolType = LiloType(name = "bool", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE

    it.setAttr(name = LiloMagicMethod.BOOL, value = BoolBool)

    it.setAttr(name = LiloMagicMethod.AND, value = BoolAnd)
    it.setAttr(name = LiloMagicMethod.OR, value = BoolOr)
}

data class LiloBool(val value: Boolean) : LiloObject(liloBoolType) {
    override fun toString() = if (value) "True" else "False"
}

private object BoolBool : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        val self = args[0] as LiloBool
        return LiloResult.Success(data = self)
    }
}

private object BoolAnd : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[0] !is LiloBool || args[1] !is LiloBool) {
            LiloResult.Failure(error = LiloExceptionMessage("Op `AND` expects rhs and lhs to be Bool"))
        }
        val self = args[0] as LiloBool
        val other = args[1] as LiloBool
        return LiloResult.Success(data = LiloBool(value = self.value.and(other.value)))
    }
}


private object BoolOr : LiloObject(liloMethodType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        if (args.size != 2 || args[0] !is LiloBool || args[1] !is LiloBool) {
            LiloResult.Failure(error = LiloExceptionMessage("Op `OR` expects rhs and lhs to be Bool"))
        }
        val self = args[0] as LiloBool
        val other = args[1] as LiloBool
        return LiloResult.Success(data = LiloBool(value = self.value.or(other.value)))
    }
}

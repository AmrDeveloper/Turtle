package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloStr
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloIntType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'int'>"

    init {
        define(name = LiloMagicMethod.ADD, value = AddMethod)
        define(name = LiloMagicMethod.SUB, value = SubMethod)
        define(name = LiloMagicMethod.MUL, value = MulMethod)
        define(name = LiloMagicMethod.DIV, value = DivMethod)
        define(name = LiloMagicMethod.MOD, value = ModMethod)

        define(name = LiloMagicMethod.STR, value = StrMethod)
    }

    private object AddMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0]
            val rhs = args[1]
            if (lhs is LiloInt && rhs is LiloInt) {
                return LiloResult.Success(data = LiloInt(value = lhs.value + rhs.value))
            }
            return LiloResult.Failure(error = RuntimeException("Op `+` is unsupported between lhs & rhs"))
        }
    }

    private object SubMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0]
            val rhs = args[1]
            if (lhs is LiloInt && rhs is LiloInt) {
                return LiloResult.Success(data = LiloInt(value = lhs.value - rhs.value))
            }
            return LiloResult.Failure(error = RuntimeException("Op `-` is unsupported between lhs & rhs"))
        }
    }

    private object MulMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0]
            val rhs = args[1]
            if (lhs is LiloInt && rhs is LiloInt) {
                return LiloResult.Success(data = LiloInt(value = lhs.value * rhs.value))
            }
            return LiloResult.Failure(error = RuntimeException("Op `*` is unsupported between lhs & rhs"))
        }
    }

    private object DivMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0]
            val rhs = args[1]
            if (lhs is LiloInt && rhs is LiloInt) {
                return LiloResult.Success(data = LiloInt(value = lhs.value / rhs.value))
            }
            return LiloResult.Failure(error = RuntimeException("Op `/` is unsupported between lhs & rhs"))
        }
    }

    private object ModMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0]
            val rhs = args[1]
            if (lhs is LiloInt && rhs is LiloInt) {
                return LiloResult.Success(data = LiloInt(value = lhs.value % rhs.value))
            }
            return LiloResult.Failure(error = RuntimeException("Op `%` is unsupported between lhs & rhs"))
        }
    }

    private object StrMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0] as LiloInt
            return LiloResult.Success(data = LiloStr(value = lhs.value.toString()))
        }
    }
}
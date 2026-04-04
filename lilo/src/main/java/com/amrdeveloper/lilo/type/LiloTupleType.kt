package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloList
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.LiloTuple
import com.amrdeveloper.lilo.runtime.LiloException
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloTupleType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'tuple'>"

    init {
        define(name = LiloMagicMethod.GET_ITEM, value = GetItemMethod)

        define(name = LiloMagicMethod.LEN, value = LenMethod)
    }

    private object GetItemMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0]
            val index = args[1]
            if (index !is LiloInt) return LiloResult.Failure(error = LiloException("Tuple index must be int"))
            val list = lhs as LiloTuple
            val item = list.values[index.value]
            return LiloResult.Success(data = item)
        }
    }

    private object LenMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val self = args[0]
            if (self !is LiloTuple) return LiloResult.Failure(error = LiloException("Expected type to be List"))
            return LiloResult.Success(data = LiloInt(value = self.values.size))
        }
    }
}
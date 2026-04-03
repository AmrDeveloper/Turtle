package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloCallable
import com.amrdeveloper.lilo.`object`.LiloInt
import com.amrdeveloper.lilo.`object`.LiloList
import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.runtime.LiloInterpreter

object LiloListType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'list'>"

    init {
        define(name = LiloMagicMethod.GET_ITEM, value = GetItemMethod)
    }

    private object GetItemMethod : LiloCallable {
        override fun invoke(
            interpreter: LiloInterpreter,
            args: List<LiloObject>
        ): LiloResult<LiloObject> {
            val lhs = args[0]
            val index = args[1]
            if (index !is LiloInt) {
                return LiloResult.Failure(error = RuntimeException("Index must be int type"))
            }

            val list = lhs as LiloList
            val item = list.values[index.value]
            return LiloResult.Success(data = item)
        }
    }
}
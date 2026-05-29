package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

data class LiloType(
    val name: String,
    val bases: List<LiloType> = mutableListOf()
) : LiloObject() {

    override val dict: MutableMap<String, LiloObject> = mutableMapOf()

    override fun getAttr(name: String): LiloObject? {
        dict[name]?.let { return it }

        for (base in bases) {
            val found = base.getAttr(name)
            if (found != null) return found
        }

        return null
    }

    override fun hasAttr(name: String): Boolean {
        if (dict.containsKey(name)) return true

        for (base in bases) {
            val found = base.getAttr(name)
            if (found != null) return true
        }

        return false
    }

    fun isSubclass(parent : LiloType) : Boolean {
        if (this == parent) return true
        for (base in bases) {
            if (base.isSubclass(parent))
                return true
        }
        return false
    }

    override fun toString(): String = "<class '$name'>"
}

object LiloBaseType {

    val LILO_OBJECT_TYPE = LiloType(name = "object")
    val LILO_TYPE_TYPE = LiloType(name = "type", bases = listOf(LILO_OBJECT_TYPE))

    init {
        LILO_OBJECT_TYPE.type = LILO_TYPE_TYPE
        LILO_TYPE_TYPE.type = LILO_TYPE_TYPE

        LILO_OBJECT_TYPE.setAttr(name = LiloMagicMethod.STR, value = ObjectStr)
    }
}

private object ObjectStr : LiloObject(liloFunctionType), LiloCallable {
    override fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject> {
        return LiloResult.Success(data = LiloStr(value = args[0].toString()))
    }
}

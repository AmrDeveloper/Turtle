package com.amrdeveloper.lilo.objects

import com.amrdeveloper.lilo.common.LiloMagicMethod
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.common.toFailure
import com.amrdeveloper.lilo.common.valueOr
import com.amrdeveloper.lilo.runtime.LiloCallable
import com.amrdeveloper.lilo.runtime.LiloInterpreter

open class LiloObject(var type: LiloType? = null) {

    open val dict: MutableMap<String, LiloObject> = mutableMapOf()

    open fun getAttr(name: String): LiloObject? {
        dict[name]?.let { return it }
        val t = type ?: return null
        if (this == type) return null
        return t.getAttr(name)
    }

    open fun setAttr(name: String, value: LiloObject) {
        dict[name] = value
    }

    open fun hasAttr(name: String): Boolean {
        if (dict.containsKey(name)) return true
        dict[name]?.let { return true }
        val t = type ?: return false
        if (this == type) return false
        return t.hasAttr(name)
    }
}

// Checking if this object can evaluate to true
// Inspired by PyObject_IsTrue in CPython
fun LiloObject.isTrue(interpreter: LiloInterpreter) : LiloResult<Boolean> {
    if (this is LiloBool) return LiloResult.Success(data = this.value)
    if (this is LiloNone) return LiloResult.Success(data = false)

    val boolMethod = getAttr(name = LiloMagicMethod.BOOL)
    if (boolMethod != null && boolMethod is LiloCallable) {
        val boolRet = boolMethod.invoke(interpreter = interpreter, args = listOf(this))
            .valueOr { return it.toFailure() }
        if (boolRet !is LiloBool) {
            throw createLiloException(liloTypeErrorType, "__bool__ should return bool, returned ${boolRet.type}")
        }
        return LiloResult.Success(data = boolRet.value)
    }

    val lenMethod = getAttr(name = LiloMagicMethod.LEN)
    if (lenMethod != null && lenMethod is LiloCallable) {
        val lenRet = lenMethod.invoke(interpreter = interpreter, args = listOf(this))
            .valueOr { return it.toFailure() }
        if (lenRet !is LiloInt) {
            throw createLiloException(liloTypeErrorType, "'${lenRet.type}' object cannot be interpreted as an integer")
        }
        return LiloResult.Success(data = lenRet.value > 0)
    }

    return LiloResult.Success(data = true)
}

// Return true if two lilo objects are equals
fun LiloObject.eq(interpreter: LiloInterpreter, other : LiloObject) : LiloResult<Boolean> {
    if (this == other) return LiloResult.Success(data = true)

    val boolMethod = getAttr(name = LiloMagicMethod.EQ)
    if (boolMethod != null && boolMethod is LiloCallable) {
        val boolRet = boolMethod.invoke(interpreter = interpreter, args = listOf(this, other))
            .valueOr { return it.toFailure() }
        if (boolRet !is LiloBool) {
            throw createLiloException(liloTypeErrorType, "__bool__ should return bool, returned ${boolRet.type}")
        }
        return LiloResult.Success(data = boolRet.value)
    }

    throw createLiloException(liloTypeErrorType, "__bool__ can't be applied between ${this.type.toString()} and ${other.type.toString()}")
}

// Return the string representation of the LiloObject
fun LiloObject.str(interpreter: LiloInterpreter) : LiloResult<String> {
    val boolMethod = getAttr(name = LiloMagicMethod.STR)
    if (boolMethod != null && boolMethod is LiloCallable) {
        val boolRet = boolMethod.invoke(interpreter = interpreter, args = listOf(this))
            .valueOr { return it.toFailure() }
        if (boolRet !is LiloStr) {
            throw createLiloException(liloTypeErrorType, "__str__ should return str, returned ${boolRet.type}")
        }
        return LiloResult.Success(data = boolRet.value)
    }

    return LiloResult.Success(data = toString())
}

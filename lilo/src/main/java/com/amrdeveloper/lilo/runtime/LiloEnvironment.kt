package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.`object`.LiloObject
import com.amrdeveloper.lilo.`object`.liloBaseExceptionType
import com.amrdeveloper.lilo.`object`.liloStopIteratorType
import com.amrdeveloper.lilo.type.liloBoolType
import com.amrdeveloper.lilo.type.liloDictType
import com.amrdeveloper.lilo.type.liloFloatType
import com.amrdeveloper.lilo.type.liloIntType
import com.amrdeveloper.lilo.type.liloListType
import com.amrdeveloper.lilo.type.liloSetType
import com.amrdeveloper.lilo.type.liloStrType
import com.amrdeveloper.lilo.type.liloTupleType

class LiloEnvironment(val enclosing: LiloEnvironment?) {

    val values = mutableMapOf<String, LiloObject>()

    init {
        values["bool"] = liloBoolType
        values["int"] = liloIntType
        values["float"] = liloFloatType
        values["str"] = liloStrType
        values["tuple"] = liloTupleType
        values["list"] = liloListType
        values["set"] = liloSetType
        values["dict"] = liloDictType

        values["BaseException"] = liloBaseExceptionType
        values["StopIterator"] = liloStopIteratorType
    }

    fun define(name: String, value: LiloObject) {
        values[name] = value
    }

    fun get(name: String): LiloObject? {
        if (values.containsKey(name)) {
            return values[name]
        }

        if (enclosing != null) {
            return enclosing.get(name)
        }

        return null
    }

    fun assign(name: String, value: LiloObject): Boolean {
        if (values.containsKey(name)) {
            values[name] = value
            return true
        }

        if (enclosing != null) {
            return enclosing.assign(name, value)
        }

        return false
    }

}

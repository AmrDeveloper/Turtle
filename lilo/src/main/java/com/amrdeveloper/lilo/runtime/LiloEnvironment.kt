package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.`object`.LiloObject

class LiloEnvironment(val enclosing: LiloEnvironment?) {

    val values = mutableMapOf<String, LiloObject>()

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
package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.value.LiloValue

class LiloEnvironment(val enclosing: LiloEnvironment?) {

    val values = mutableMapOf<String, LiloValue>()

    fun define(name: String, value: LiloValue) {
        values[name] = value
    }

    fun get(name: String): LiloValue? {
        if (values.containsKey(name)) {
            return values[name]
        }

        if (enclosing != null) {
            return enclosing.get(name)
        }

        return null
    }

    fun assign(name: String, value: LiloValue): Boolean {
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
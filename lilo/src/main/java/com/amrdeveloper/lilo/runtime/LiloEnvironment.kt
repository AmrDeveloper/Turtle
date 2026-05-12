package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.`object`.LiloObject

class LiloEnvironment(val enclosing: LiloEnvironment? = null) {

    val values = mutableMapOf<String, LiloObject>()

    fun set(name: String, value: LiloObject) {
        values[name] = value
    }

    fun get(name: String): LiloObject? {
        if (values.containsKey(name)) return values[name]
        if (enclosing != null) return enclosing.get(name)
        return null
    }

    fun defineNonLocal(name : String) {
        val value = get(name)!!
        set(name, value)
    }
}

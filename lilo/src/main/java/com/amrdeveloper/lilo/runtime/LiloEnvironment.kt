package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.`object`.LiloObject

class LiloEnvironment(val enclosing: LiloEnvironment? = null) {

    val values = mutableMapOf<String, LiloObject>()
    val globals = mutableSetOf<String>()

    companion object {
        val builtins = mutableMapOf<String, LiloObject>()
    }

    fun set(name: String, value: LiloObject) {
        if (isGlobal(name)) {
            setGlobal(name, value)
            return
        }
        values[name] = value
    }

    fun setGlobal(name: String, value : LiloObject) {
        val globals = globalScope()
        globals.values[name] = value
    }

    fun get(name: String): LiloObject? {
        if (isGlobal(name)) return getGlobal(name)
        return values[name]
    }

    fun getGlobal(name: String): LiloObject? {
        val globals = globalScope()
        return globals.values[name]
    }

    fun markGlobal(name : String) {
        globals.add(name)
    }

    fun isGlobal(name : String) : Boolean {
        if (globals.contains(name)) return true;
        if (enclosing != null) return enclosing.isGlobal(name)
        return false
    }

    fun globalScope() : LiloEnvironment {
        var scope =  this
        while (scope.enclosing != null) scope = scope.enclosing
        return scope
    }
}

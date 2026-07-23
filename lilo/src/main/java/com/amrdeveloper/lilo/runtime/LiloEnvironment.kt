package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.objects.LiloObject

class LiloEnvironment(val enclosing: LiloEnvironment? = null) {

    val values = mutableMapOf<String, LiloObject>()
    val globals = mutableSetOf<String>()

    companion object {
        val modules = mutableMapOf<String, LiloObject>()
        val builtins = mutableMapOf<String, LiloObject>()
    }

    fun set(name: String, value: LiloObject) {
        if (isMarkedGlobal(name)) {
            setGlobal(name, value)
            return
        }
        values[name] = value
    }

    fun setGlobal(name: String, value : LiloObject) {
        markGlobal(name)
        val globals = globalScope()
        globals.values[name] = value
    }

    fun get(name: String): LiloObject? {
        if (isMarkedGlobal(name)) return getGlobal(name)
        if (values.containsKey(name)) return values[name]
        return enclosing?.get(name)
    }

    fun getGlobal(name: String): LiloObject? {
        val globals = globalScope()
        return globals.values[name]
    }

    fun markGlobal(name : String) {
        globals.add(name)
    }

    fun isMarkedGlobal(name : String) : Boolean {
        return globals.contains(name)
    }

    fun delete(name : String) : LiloObject? {
        return values.remove(key = name)
    }

    fun deleteGlobal(name: String): LiloObject? {
        val globals = globalScope()
        return globals.values.remove(key = name)
    }

    fun globalScope() : LiloEnvironment {
        var scope =  this
        while (scope.enclosing != null) scope = scope.enclosing
        return scope
    }

    fun isGlobalScope() = this.enclosing == null
    fun isLocalScope() = this.enclosing != null
}

package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloType

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
        if (this == type) return false
        return t.hasAttr(name)
    }
}

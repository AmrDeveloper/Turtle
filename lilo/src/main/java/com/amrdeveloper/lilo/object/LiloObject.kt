package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloType

open class LiloObject(var type: LiloType? = null) {

    open val dict: MutableMap<String, LiloObject> = mutableMapOf()

    open fun getAttr(name: String): LiloObject? {
        dict[name]?.let { return it }
        val t = type ?: return null
        return t.getAttr(name)
    }

    open fun setAttr(name: String, value: LiloObject) {
        dict[name] = value
    }
}

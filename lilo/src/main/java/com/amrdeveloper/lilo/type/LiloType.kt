package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.`object`.LiloObject

interface LiloType {
    val attributes : MutableMap<String, LiloObject>

    fun define(name: String, value: LiloObject) {
        attributes[name] = value
    }

    fun lookup(name: String): LiloObject? {
        return attributes[name]
    }
}


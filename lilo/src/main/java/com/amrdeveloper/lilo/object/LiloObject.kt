package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloType

sealed interface LiloObject {
    val type : LiloType

    fun lookup(name : String) : LiloObject? {
        return type.lookup(name)
    }
}
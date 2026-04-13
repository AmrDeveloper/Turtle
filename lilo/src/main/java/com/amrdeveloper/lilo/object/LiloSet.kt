package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloSetType

class LiloSet(val values: MutableSet<LiloObject>) : LiloObject(liloSetType) {
    override fun toString(): String {
        return "{".plus(values.joinToString(", ") { it.toString() }).plus("}")
    }
}

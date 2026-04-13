package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloListType

data class LiloList(val values: MutableList<LiloObject>) : LiloObject(liloListType) {
    override fun toString(): String {
        return "[".plus(values.joinToString(", ") { it.toString() }).plus("]")
    }
}

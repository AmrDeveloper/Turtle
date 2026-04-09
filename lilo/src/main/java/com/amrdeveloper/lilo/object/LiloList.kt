package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloListType

class LiloList(val values: List<LiloObject>) : LiloObject(liloListType) {
    override fun toString(): String {
        return "[".plus(values.joinToString(", ") { it.toString() }).plus("]")
    }
}

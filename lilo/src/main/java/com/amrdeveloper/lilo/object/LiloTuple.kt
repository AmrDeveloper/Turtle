package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloTupleType

data class LiloTuple(val values: List<LiloObject>) : LiloObject(liloTupleType) {
    override fun toString(): String {
        return "(".plus(values.joinToString(", ") { it.toString() }).plus(")")
    }
}

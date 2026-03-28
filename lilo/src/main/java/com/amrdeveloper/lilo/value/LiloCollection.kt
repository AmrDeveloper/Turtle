package com.amrdeveloper.lilo.value

sealed interface LiloCollection : LiloValue

class LiloList(val values: List<LiloValue>) : LiloCollection {
    override fun toString(): String {
        return "[".plus(values.joinToString(", ")).plus("]")
    }
}

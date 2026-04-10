package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloFloatType

class LiloFloat(val value: Float) : LiloObject(liloFloatType) {
    override fun toString() = when (value) {
        Float.POSITIVE_INFINITY -> "inf"
        else -> value.toString()
    }
}

package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloFloatType

data class LiloFloat(val value: Float) : LiloObject(liloFloatType) {
    override fun toString(): String {
        if (value == Float.POSITIVE_INFINITY) return "inf"
        if (value.isNaN()) return "nan"
        return value.toString()
    }
}

package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloBoolType
import com.amrdeveloper.lilo.type.LiloFloatType
import com.amrdeveloper.lilo.type.LiloIntType
import com.amrdeveloper.lilo.type.LiloType

sealed interface LiloNumber : LiloObject

fun LiloNumber.isInt(): Boolean {
    return when (this) {
        is LiloInt -> true
        is LiloFloat -> false
        is LiloBool -> false
    }
}

fun LiloNumber.isFloat(): Boolean {
    return when (this) {
        is LiloInt -> false
        is LiloFloat -> true
        is LiloBool -> false
    }
}

fun LiloNumber.asInt(): Int {
    return when (this) {
        is LiloInt -> this.value
        is LiloFloat -> this.value.toInt()
        is LiloBool -> if (this.value) 1 else 0
    }
}

fun LiloNumber.asFloat(): Float {
    return when (this) {
        is LiloInt -> this.value.toFloat()
        is LiloFloat -> this.value
        is LiloBool -> if (this.value) 1.0f else 0.0f
    }
}

class LiloInt(val value: Int) : LiloNumber {
    override val type: LiloType = LiloIntType
}

class LiloFloat(val value: Float) : LiloNumber {
    override val type: LiloType = LiloFloatType
}

class LiloBool(val value: Boolean) : LiloNumber {
    override val type: LiloType = LiloBoolType
}


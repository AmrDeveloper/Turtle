package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloBoolType
import com.amrdeveloper.lilo.type.LiloFloatType
import com.amrdeveloper.lilo.type.LiloIntType
import com.amrdeveloper.lilo.type.LiloType

sealed interface LiloNumber : LiloObject

class LiloInt(val value: Int) : LiloNumber {
    override val type: LiloType = LiloIntType
}

class LiloFloat(val value: Float) : LiloNumber {
    override val type: LiloType = LiloFloatType
}

class LiloBool(val value: Boolean) : LiloNumber {
    override val type: LiloType = LiloBoolType
}


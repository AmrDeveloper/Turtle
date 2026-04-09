package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloIntType

class LiloInt(val value: Int) : LiloObject(liloIntType) {
    override fun toString() = value.toString()
}

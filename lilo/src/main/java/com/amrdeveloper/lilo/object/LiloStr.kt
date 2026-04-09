package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloStrType

class LiloStr(val value: String) : LiloObject(liloStrType) {
    override fun toString() = value
}

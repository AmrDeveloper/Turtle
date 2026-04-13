package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloStrType

data class LiloStr(val value: String) : LiloObject(liloStrType) {
    override fun toString() = value
}

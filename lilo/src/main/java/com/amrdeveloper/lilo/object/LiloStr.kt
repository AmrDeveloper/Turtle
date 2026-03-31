package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloStrType
import com.amrdeveloper.lilo.type.LiloType

class LiloStr(val value: String) : LiloObject {
    override val type: LiloType = LiloStrType

    override fun toString(): String {
        return value
    }
}
package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloIntType

data class LiloRange(
    val start: Int = 0,
    val stop: Int,
    val step: Int = 1
) : LiloObject(liloIntType) {
    override fun toString() = "range($start, $stop, $step)"
}

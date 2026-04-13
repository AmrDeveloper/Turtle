package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloNoneType

data class LiloNone(private val unit: Unit = Unit) : LiloObject(liloNoneType) {
    override fun toString() = "None"
}

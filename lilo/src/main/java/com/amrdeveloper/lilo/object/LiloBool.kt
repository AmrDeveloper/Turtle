package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloBoolType

data class LiloBool(val value: Boolean) : LiloObject(liloBoolType) {
    override fun toString() = if (value) "True" else "False"
}

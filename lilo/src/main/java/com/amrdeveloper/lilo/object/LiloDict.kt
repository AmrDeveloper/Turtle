package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloDictType

class LiloDict(val values: MutableMap<LiloObject, LiloObject>) : LiloObject(liloDictType) {
    override fun toString(): String {
        val buffer = StringBuilder()
        buffer.append("{")
        var i = 0
        for ((key, value) in values) {
            buffer.append("$key:$value")
            if (i != values.size - 1) buffer.append(", ")
            i++
        }
        buffer.append("}")
        return buffer.toString()
    }
}

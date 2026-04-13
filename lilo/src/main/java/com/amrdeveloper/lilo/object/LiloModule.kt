package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloModuleType

data class LiloModule(val name: String) : LiloObject(liloModuleType) {
    override fun toString() = "<module ${name}>"
}

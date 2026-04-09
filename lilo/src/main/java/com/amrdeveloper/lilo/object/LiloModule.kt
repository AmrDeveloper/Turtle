package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloModuleType

class LiloModule(val name: String) : LiloObject(liloModuleType) {
    override fun toString() = "<module ${name}>"
}

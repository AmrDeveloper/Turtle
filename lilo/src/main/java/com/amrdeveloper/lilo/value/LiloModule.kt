package com.amrdeveloper.lilo.value

import com.amrdeveloper.lilo.std.core.LiloStdModule

class LiloModule(val name: String, val module: LiloStdModule) : LiloValue {
    override fun toString() = "<module $name>"
}
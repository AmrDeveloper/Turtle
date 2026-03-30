package com.amrdeveloper.lilo.value

class LiloModule(val name: String) : LiloValue {
    override fun toString() = "<module $name>"
}
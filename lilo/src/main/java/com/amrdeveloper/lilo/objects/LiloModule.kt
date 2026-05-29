package com.amrdeveloper.lilo.objects

val liloModuleType = LiloType(name = "module", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

data class LiloModule(val name: String) : LiloObject(liloModuleType) {
    override fun toString() = "<module ${name}>"
}

package com.amrdeveloper.lilo.type

val liloModuleType = LiloType(name = "module", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

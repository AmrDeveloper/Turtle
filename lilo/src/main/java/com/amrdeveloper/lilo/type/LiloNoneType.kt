package com.amrdeveloper.lilo.type

val liloNoneType = LiloType(name = "NoneType", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

package com.amrdeveloper.lilo.type

val liloFloatType = LiloType(name = "float", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

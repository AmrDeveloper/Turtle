package com.amrdeveloper.lilo.type

val liloStrType = LiloType(name = "str", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

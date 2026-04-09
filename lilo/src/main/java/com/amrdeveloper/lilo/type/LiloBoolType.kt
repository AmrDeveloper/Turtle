package com.amrdeveloper.lilo.type

val liloBoolType = LiloType(name = "bool", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

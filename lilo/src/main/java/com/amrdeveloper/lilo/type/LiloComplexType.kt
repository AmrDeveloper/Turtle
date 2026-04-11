package com.amrdeveloper.lilo.type

val liloComplexType =
    LiloType(name = "complex", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

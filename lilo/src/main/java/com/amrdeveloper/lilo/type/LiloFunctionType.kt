package com.amrdeveloper.lilo.type

val liloFunctionType =
    LiloType(name = "function", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

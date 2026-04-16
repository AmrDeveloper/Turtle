package com.amrdeveloper.lilo.type

val liloMethodType =
    LiloType(name = "method", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

package com.amrdeveloper.lilo.std.core

import com.amrdeveloper.lilo.value.LiloValue

interface LiloStdObject

interface LiloStdModule : LiloStdObject {
    fun lookup(name: String): LiloValue?
}
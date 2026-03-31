package com.amrdeveloper.lilo.std.core

interface LiloStdObject

interface LiloStdModule : LiloStdObject {
    fun lookup(name: String): LiloStdFunction?
}
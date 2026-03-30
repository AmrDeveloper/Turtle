package com.amrdeveloper.lilo.std.core

interface LiloStdObject

interface LiloStdModule : LiloStdObject {
    fun getStdFunction(name: String): LiloStdFunction?
}
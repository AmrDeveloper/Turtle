package com.amrdeveloper.lilo.std

sealed interface LiloStdObject

sealed interface LiloStdModule : LiloStdObject {
    fun getStdFunction(name: String): LiloStdFunction?
}
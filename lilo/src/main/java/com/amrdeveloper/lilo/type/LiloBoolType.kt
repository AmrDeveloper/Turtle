package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.`object`.LiloObject

object LiloBoolType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'bool'>"
}
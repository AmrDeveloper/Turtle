package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.`object`.LiloObject

object LiloStrType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'str'>"
}
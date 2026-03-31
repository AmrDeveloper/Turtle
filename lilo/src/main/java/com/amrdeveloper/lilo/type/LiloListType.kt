package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.`object`.LiloObject

object LiloListType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'list'>"
}
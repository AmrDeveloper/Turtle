package com.amrdeveloper.lilo.type

import com.amrdeveloper.lilo.`object`.LiloObject

class LiloModuleType : LiloType {
    override val attributes = mutableMapOf<String, LiloObject>()
    override fun toString() = "<class 'module'>"
}
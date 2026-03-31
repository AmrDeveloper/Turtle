package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloModuleType
import com.amrdeveloper.lilo.type.LiloType

class LiloModule(val name: String) : LiloObject {
    override val type: LiloType = LiloModuleType()
}
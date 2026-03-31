package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloListType
import com.amrdeveloper.lilo.type.LiloType

sealed interface LiloCollection : LiloObject

class LiloList(val values: List<LiloObject>) : LiloCollection {
    override val type: LiloType = LiloListType
}

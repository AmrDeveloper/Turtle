package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.LiloTupleType
import com.amrdeveloper.lilo.type.LiloType

class LiloTuple(val values: List<LiloObject>) : LiloCollection {
    override val type: LiloType = LiloTupleType
}

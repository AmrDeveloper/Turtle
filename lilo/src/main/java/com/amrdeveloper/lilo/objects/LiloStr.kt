package com.amrdeveloper.lilo.objects

val liloStrType = LiloType(name = "str", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
    it.type = LiloBaseType.LILO_TYPE_TYPE
}

data class LiloStr(val value: String) : LiloObject(liloStrType) {
    override fun toString() = value
}

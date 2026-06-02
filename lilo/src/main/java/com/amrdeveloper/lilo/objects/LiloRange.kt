package com.amrdeveloper.lilo.objects

val liloRangeType = LiloType(name = "range", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE))

data class LiloRange(
    val start: Int = 0,
    val stop: Int,
    val step: Int = 1
) : LiloObject(liloRangeType) {
    override fun toString() = "range($start, $stop, $step)"
}

package com.amrdeveloper.lilo.objects

val liloComplexType =
    LiloType(name = "complex", bases = listOf(LiloBaseType.LILO_OBJECT_TYPE)).also {
        it.type = LiloBaseType.LILO_TYPE_TYPE
    }

data class LiloComplex(val real: Double, val imag: Double) : LiloObject(liloComplexType) {
    override fun toString() = "($real+${imag}j)"
}

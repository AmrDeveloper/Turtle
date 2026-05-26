package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloComplexType

data class LiloComplex(val real: Double, val imag: Double) : LiloObject(liloComplexType) {
    override fun toString() = "($real+${imag}j)"
}

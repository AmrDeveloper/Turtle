package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.type.liloComplexType

data class LiloComplex(val real: Float, val imag: Float) : LiloObject(liloComplexType) {
    override fun toString() = "($real+${imag}j)"
}

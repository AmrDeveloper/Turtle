package com.amrdeveloper.lilo.opertion

import com.amrdeveloper.lilo.common.LiloResult

sealed interface LiloOperation<T> {
    fun run(): LiloResult<T>
}
package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.value.LiloValue

sealed interface LiloStdFunction : LiloStdObject {
    fun call(args: List<LiloValue>): LiloResult<LiloValue>
}
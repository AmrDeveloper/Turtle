package com.amrdeveloper.lilo.std.core

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.value.LiloValue

interface LiloStdFunction : LiloStdObject {
    fun call(args: List<LiloValue>): LiloResult<LiloValue>
}
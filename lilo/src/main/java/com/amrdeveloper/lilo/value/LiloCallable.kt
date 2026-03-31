package com.amrdeveloper.lilo.value

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloInterpreter

interface LiloCallable : LiloValue {
    fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloValue>
    ): LiloResult<LiloValue>
}
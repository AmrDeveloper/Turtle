package com.amrdeveloper.lilo.`object`

import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.runtime.LiloInterpreter
import com.amrdeveloper.lilo.type.LiloCallableType
import com.amrdeveloper.lilo.type.LiloType

interface LiloCallable : LiloObject {
    override val type: LiloType
        get() = LiloCallableType

    fun invoke(
        interpreter: LiloInterpreter,
        args: List<LiloObject>
    ): LiloResult<LiloObject>
}
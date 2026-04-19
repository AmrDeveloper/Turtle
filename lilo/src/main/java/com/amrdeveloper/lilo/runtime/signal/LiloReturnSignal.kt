package com.amrdeveloper.lilo.runtime.signal

import com.amrdeveloper.lilo.`object`.LiloObject

class LiloReturnSignal(val value: LiloObject? = null) : RuntimeException()

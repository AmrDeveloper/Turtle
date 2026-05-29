package com.amrdeveloper.lilo.runtime.signal

import com.amrdeveloper.lilo.objects.LiloObject

class LiloReturnSignal(val value: LiloObject? = null) : RuntimeException()

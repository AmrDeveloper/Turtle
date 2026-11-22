package com.amrdeveloper.lilo.runtime.signal

import com.amrdeveloper.lilo.objects.LiloObject

abstract class LiloSignal() : RuntimeException()

class LiloReturnSignal(val value: LiloObject? = null) : LiloSignal()

class LiloBreakSignal : LiloSignal()

class LiloContinueSignal : LiloSignal()

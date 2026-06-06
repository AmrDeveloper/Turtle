package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.objects.LiloObject

class LiloExceptionMessage(val message: String)

class LiloRaise(val exception : LiloObject) : RuntimeException()

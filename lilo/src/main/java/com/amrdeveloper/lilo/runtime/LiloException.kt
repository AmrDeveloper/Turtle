package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.objects.EXCEPTION_CAUSE_FIELD
import com.amrdeveloper.lilo.objects.LiloObject

class LiloExceptionMessage(val message: String) {
    override fun toString() = message
}

class LiloRaise(val exception : LiloObject) : RuntimeException() {
    override fun toString(): String {
        var message = "raise ${exception.type?.name}"
        val cause = exception.getAttr(name = EXCEPTION_CAUSE_FIELD)
        if (cause != null) message += " from ${cause.type?.name}"
        return message
    }
}

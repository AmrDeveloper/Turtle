package com.amrdeveloper.lilo.runtime

import com.amrdeveloper.lilo.`object`.LiloObject

class LiloExceptionMessage(val message: String) {
    override fun toString() = message
}

class LiloRaise(val exception : LiloObject) : RuntimeException() {
    override fun toString(): String {
        var message = "raise ${exception.type?.name}"
        val cause = exception.getAttr(name = "cause")
        if (cause != null) message += " from ${cause.type?.name}"
        return message
    }
}

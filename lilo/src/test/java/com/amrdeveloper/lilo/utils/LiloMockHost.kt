package com.amrdeveloper.lilo.utils

import com.amrdeveloper.lilo.machine.host.LiloAbstractHost

class LiloMockHost : LiloAbstractHost {
    var buffer = StringBuilder()

    override fun write(message: String) {
        buffer.append(message)
    }

    fun clear() {
        buffer = buffer.clear()
    }
}

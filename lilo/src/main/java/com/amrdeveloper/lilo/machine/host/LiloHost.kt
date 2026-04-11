package com.amrdeveloper.lilo.machine.host

class LiloHost(val onStdout: (String) -> Unit) : LiloAbstractHost {

    override fun write(message: String) {
        onStdout(message)
    }
}

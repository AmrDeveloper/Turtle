package com.amrdeveloper.lilo.utils

import com.amrdeveloper.lilo.machine.LiloAbstractMachine

class LiloMockMachine : LiloAbstractMachine {

    private val host = LiloMockHost()

    override fun getHost() = host

    override fun getScreen() = null

    override fun getGPU() = null
    override fun hasGpu() = false
}

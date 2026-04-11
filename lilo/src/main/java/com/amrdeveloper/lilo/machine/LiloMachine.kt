package com.amrdeveloper.lilo.machine

import com.amrdeveloper.lilo.machine.device.LiloAbstractGPU
import com.amrdeveloper.lilo.machine.host.LiloAbstractHost

class LiloMachine(
    val liloHost: LiloAbstractHost,
    val liloGPU: LiloAbstractGPU? = null
) : LiloAbstractMachine {

    suspend fun initMachine() {
        liloGPU?.initWebGPU()
    }

    override fun getHost() = liloHost
    override fun getGPU() = liloGPU
    override fun hasGpu() = liloGPU != null
}

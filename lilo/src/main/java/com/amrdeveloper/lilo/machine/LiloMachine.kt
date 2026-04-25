package com.amrdeveloper.lilo.machine

import com.amrdeveloper.lilo.machine.device.LiloAbstractGPU
import com.amrdeveloper.lilo.machine.host.LiloAbstractHost
import com.amrdeveloper.lilo.machine.screen.LiloAbstractScreen

class LiloMachine(
    val liloHost: LiloAbstractHost,
    val liloScreen: LiloAbstractScreen? = null,
    val liloGPU: LiloAbstractGPU? = null,
) : LiloAbstractMachine {

    suspend fun initMachine() {
        liloGPU?.initWebGPU()
    }

    override fun getHost() = liloHost
    override fun getScreen() = liloScreen
    override fun getGPU() = liloGPU
    override fun hasGpu() = liloGPU != null
}

package com.amrdeveloper.lilo.machine

import com.amrdeveloper.lilo.machine.device.LiloAbstractGPU
import com.amrdeveloper.lilo.machine.host.LiloAbstractHost

interface LiloAbstractMachine {
    fun getHost(): LiloAbstractHost
    fun getGPU(): LiloAbstractGPU?
    fun hasGpu(): Boolean
}

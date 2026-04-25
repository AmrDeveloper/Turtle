package com.amrdeveloper.lilo.machine

import com.amrdeveloper.lilo.machine.device.LiloAbstractGPU
import com.amrdeveloper.lilo.machine.host.LiloAbstractHost
import com.amrdeveloper.lilo.machine.screen.LiloAbstractScreen

interface LiloAbstractMachine {
    fun getHost(): LiloAbstractHost
    fun getScreen(): LiloAbstractScreen?
    fun getGPU(): LiloAbstractGPU?
    fun hasGpu(): Boolean
}

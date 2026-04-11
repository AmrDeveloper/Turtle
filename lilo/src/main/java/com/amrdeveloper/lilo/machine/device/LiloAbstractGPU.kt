package com.amrdeveloper.lilo.machine.device

interface LiloAbstractGPU {
    suspend fun initWebGPU()
    suspend fun runKernal(kernal: LiloKernal): Any
    fun deinitWebGPU()
    fun getWebGPU(): Any
    fun getGPUDevice(): Any
}

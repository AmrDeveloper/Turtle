package com.amrdeveloper.lilo.machine.device

import androidx.webgpu.GPUDevice
import androidx.webgpu.helper.WebGpu

interface LiloAbstractGPU {
    suspend fun initWebGPU()
    suspend fun runKernal(kernal: LiloKernal): Any
    fun deinitWebGPU()
    fun getWebGPU(): WebGpu
    fun getGPUDevice(): GPUDevice
}

package com.amrdeveloper.lilo.machine.device

import androidx.webgpu.GPUDevice
import androidx.webgpu.helper.WebGpu
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.`object`.LiloObject

interface LiloAbstractGPU {
    suspend fun initWebGPU()

    suspend fun launchKernal(
        kernal: LiloKernal,
        config: LiloKernalConfig
    ): LiloResult<LiloObject>

    fun deinitWebGPU()

    fun getWebGPU(): WebGpu
    fun getGPUDevice(): GPUDevice
}

package com.amrdeveloper.lilo.machine.device

import androidx.webgpu.GPUDevice
import androidx.webgpu.helper.WebGpu
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.lib.gpu.LiloConfiguredKernal
import com.amrdeveloper.lilo.objects.LiloObject

interface LiloAbstractGPU {
    suspend fun initWebGPU()

    suspend fun launchKernal(
        gpuCode : String,
        kernal: LiloConfiguredKernal,
        args: List<LiloObject>
    ): LiloResult<LiloObject>

    fun deinitWebGPU()

    fun getWebGPU(): WebGpu
    fun getGPUDevice(): GPUDevice
}

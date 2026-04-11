package com.amrdeveloper.lilo.machine.device

import androidx.webgpu.GPUDevice
import androidx.webgpu.helper.WebGpu
import androidx.webgpu.helper.createWebGpu

class LiloWebGPU : LiloAbstractGPU {

    private lateinit var webGpu: WebGpu
    private lateinit var device: GPUDevice

    override suspend fun initWebGPU() {
        webGpu = createWebGpu()
        device = webGpu.device
    }

    override suspend fun runKernal(kernal: LiloKernal): Any {
        return kernal.run(webGpu)
    }

    override fun deinitWebGPU() {
        if (!::webGpu.isInitialized) return
        webGpu.close()
    }

    override fun getWebGPU() = webGpu
    override fun getGPUDevice() = device
}

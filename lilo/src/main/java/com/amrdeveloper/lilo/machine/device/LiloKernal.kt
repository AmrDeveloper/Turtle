package com.amrdeveloper.lilo.machine.device

import androidx.webgpu.helper.WebGpu

interface LiloKernal {
    fun run(webGpu: WebGpu): Any
}

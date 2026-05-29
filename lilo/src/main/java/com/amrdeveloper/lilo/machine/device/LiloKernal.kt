package com.amrdeveloper.lilo.machine.device

import androidx.webgpu.helper.WebGpu
import com.amrdeveloper.lilo.common.LiloResult
import com.amrdeveloper.lilo.objects.LiloObject

fun interface LiloKernal {
    fun run(
        webGpu: WebGpu,
        config: LiloKernalConfig
    ): LiloResult<LiloObject>
}

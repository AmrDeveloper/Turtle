package com.amrdeveloper.lilo.lib.gpu

import com.amrdeveloper.lilo.objects.LiloModule
import com.amrdeveloper.lilo.objects.liloKernalType

private const val MODULE_NAME = "gpu"

val liloGPUModule = LiloModule(name = MODULE_NAME).also {
    // Functions
    it.setAttr(name = "max_threads_dim", value = LiloGPUMaxThreadsDim)
    it.setAttr(name = "max_threads_per_block", value = LiloGPUMaxThreadsPerBlock)
    it.setAttr(name = "wrap_size", value = LiloGPUWrapSize)

    it.setAttr(name = "gpu", value = LiloGPUDecorator)

    // Types
    it.setAttr(name = "Dim", value = liloDimType)
    it.setAttr(name = "kernal", value = liloKernalType)
}

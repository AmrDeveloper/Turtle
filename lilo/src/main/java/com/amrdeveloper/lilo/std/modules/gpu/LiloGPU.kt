package com.amrdeveloper.lilo.std.modules.gpu

import com.amrdeveloper.lilo.`object`.LiloModule

private const val MODULE_NAME = "gpu"

val liloGPUModule = LiloModule(name = MODULE_NAME).also {
    // Functions
    it.setAttr(name = "max_threads_dim", value = LiloGPUMaxThreadsDim)
    it.setAttr(name = "max_threads_per_block", value = LiloGPUMaxThreadsPerBlock)
    it.setAttr(name = "wrap_size", value = LiloGPUWrapSize)

    // Types
    it.setAttr(name = "Dim", value = liloDimType)
}

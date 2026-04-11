package com.amrdeveloper.lilo.std.modules.gpu

import com.amrdeveloper.lilo.`object`.LiloModule

private const val MODULE_NAME = "gpu"

val liloGPUModule = LiloModule(name = MODULE_NAME).also {
    it.setAttr(name = "max_threads_per_block", value = LiloGPUMaxThreadsPerBlock)
}

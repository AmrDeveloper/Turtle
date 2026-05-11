package com.amrdeveloper.lilo.machine.device

data class LiloConfigDim3(
    val x: Int = 1,
    val y: Int = 1,
    val z: Int = 1,
)

data class LiloKernalConfig(
    // GPU Workgroup Count (X, y, z)
    val blocks: LiloConfigDim3,

    // GPU workgroup_size(x, y, z)
    val threadsPerBlock: LiloConfigDim3,
)

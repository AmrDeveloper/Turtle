package com.amrdeveloper.turtle.data

import kotlinx.coroutines.flow.Flow

interface LiloFileDataSource {
    fun getLiloFiles() : Flow<List<LiloFileEntity>>
}

package com.amrdeveloper.turtle.data

import kotlinx.coroutines.flow.Flow

interface LiloFileDataSource {
    fun getLiloFiles(keyword: String? = null) : Flow<List<LiloFileEntity>>
}

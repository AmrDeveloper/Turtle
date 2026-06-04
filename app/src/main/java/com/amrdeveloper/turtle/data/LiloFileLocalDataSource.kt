package com.amrdeveloper.turtle.data

import kotlinx.coroutines.flow.Flow

class LiloFileLocalDataSource internal constructor(
    private val fileDao: LiloFileDao,
) : LiloFileDataSource {

    override fun getLiloFiles(
        keyword: String?
    ): Flow<List<LiloFileEntity>> = fileDao.getLiloFiles(keyword)
}

package com.amrdeveloper.turtle.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LiloFileDao : BaseDao<LiloFileEntity> {

    @Query(value = "SELECT * FROM lilo_package")
    fun getLiloFiles() : Flow<List<LiloFileEntity>>
}

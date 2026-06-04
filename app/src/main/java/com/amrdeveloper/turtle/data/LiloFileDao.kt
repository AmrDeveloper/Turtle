package com.amrdeveloper.turtle.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LiloFileDao : BaseDao<LiloFileEntity> {

    @Query(value = """
        SELECT * FROM lilo_package
        WHERE ((:keyword IS NULL) OR (:keyword = ' ')
            OR (name LIKE '%' || :keyword || '%') 
            OR (sourceCode LIKE '%' || :keyword || '%'))
    """)
    fun getLiloFiles(
        keyword: String? = null,
    ) : Flow<List<LiloFileEntity>>
}

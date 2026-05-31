package com.amrdeveloper.turtle.data

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: List<T>) : Array<Long>

    @Update
    suspend fun update(item: T): Int

    @Delete
    suspend fun delete(item: T): Int
}

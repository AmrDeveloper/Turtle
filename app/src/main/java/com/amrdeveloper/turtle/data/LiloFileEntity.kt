package com.amrdeveloper.turtle.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@Entity(tableName = "lilo_package", indices = [Index(value = ["name"], unique = true)])
data class LiloFileEntity (
    var name: String,
    var sourceCode: String,
    var creationTimeStamp: Long = System.currentTimeMillis(),
    var updateTimeStamp: Long = -1,
    var isUpdated: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) : Parcelable

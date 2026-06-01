package com.amrdeveloper.turtle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "turtle_database"
private const val DATABASE_VERSION = 1

@Database(entities = [LiloFileEntity::class], version = DATABASE_VERSION)
abstract class TurtleDatabase : RoomDatabase() {

    abstract fun liloFileDao(): LiloFileDao

    companion object {

        @Volatile
        private var INSTANCE: TurtleDatabase? = null

        fun getDatabase(context: Context): TurtleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = TurtleDatabase::class.java,
                    name = DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance.populateInitialData()
                instance
            }
        }
    }

    /**
     * Populate the database with shipped examples
     */
    private fun populateInitialData() {
        CoroutineScope(context = Dispatchers.IO).launch {
            liloFileDao().insert(items = liloShippedExamples)
        }
    }
}

package com.amrdeveloper.turtle.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
     * Helper function to init default data into db once created
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun populateInitialData() {
        GlobalScope.launch(Dispatchers.Main) {

        }
    }
}

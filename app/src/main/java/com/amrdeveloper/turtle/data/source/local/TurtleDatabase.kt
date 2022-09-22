/*
 * MIT License
 *
 * Copyright (c) 2022 AmrDeveloper (Amr Hesham)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.amrdeveloper.turtle.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amrdeveloper.turtle.data.LiloPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val DATABASE_NAME = "turtle_database"
private const val DATABASE_VERSION = 1

@Database(entities = [LiloPackage::class], version = DATABASE_VERSION)
abstract class TurtleDatabase : RoomDatabase() {

    abstract fun liloPackageDao(): LiloPackageDao

    companion object {

        @Volatile
        private var INSTANCE: TurtleDatabase? = null

        fun getDatabase(context: Context): TurtleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TurtleDatabase::class.java, DATABASE_NAME
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
    private fun populateInitialData() {
        GlobalScope.launch(Dispatchers.Main) {
            liloPackageDao().insert(preloadedLiloPackages)
        }
    }
}
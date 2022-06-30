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

package com.amrdeveloper.turtle.di

import android.content.Context
import com.amrdeveloper.turtle.data.source.LiloPackageDataSource
import com.amrdeveloper.turtle.data.source.LiloPackageRepository
import com.amrdeveloper.turtle.data.source.local.LiloPackageLocalDataSource
import com.amrdeveloper.turtle.data.source.local.TurtleDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TurtleDatabase {
        return TurtleDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideLiloPackageDataSource(
        database: TurtleDatabase,
        ioDispatcher: CoroutineDispatcher
    ): LiloPackageDataSource {
        return LiloPackageLocalDataSource(database.liloPackageDao(), ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideLiloPackageRepository(dataSource: LiloPackageDataSource): LiloPackageRepository {
        return LiloPackageRepository(dataSource)
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}
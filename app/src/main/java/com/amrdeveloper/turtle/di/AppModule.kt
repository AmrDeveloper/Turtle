package com.amrdeveloper.turtle.di

import android.content.Context
import com.amrdeveloper.turtle.data.LiloFileDataSource
import com.amrdeveloper.turtle.data.LiloFileLocalDataSource
import com.amrdeveloper.turtle.data.LiloFileRepository
import com.amrdeveloper.turtle.data.TurtleDatabase
import com.amrdeveloper.turtle.ui.config.UIConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TurtleDatabase {
        return TurtleDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideLiloFileLocalDataSource(
        database: TurtleDatabase
    ) : LiloFileDataSource = LiloFileLocalDataSource(fileDao = database.liloFileDao())

    @Singleton
    @Provides
    fun provideLiloFileRepository(
        dataSource: LiloFileDataSource
    ) = LiloFileRepository(dataSource)

    @Singleton
    @Provides
    fun provideUiConfig(
        @ApplicationContext context: Context
    ) = UIConfig(context)
}

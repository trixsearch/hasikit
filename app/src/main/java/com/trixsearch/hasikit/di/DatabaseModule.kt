package com.trixsearch.hasikit.di

import android.content.Context
import androidx.room.Room
import com.trixsearch.hasikit.data.local.HasikitDatabase
import com.trixsearch.hasikit.data.local.dao.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HasikitDatabase {
        return Room.databaseBuilder(
            context,
            HasikitDatabase::class.java,
            "hasikit_db"
        )
            // Migration 1→2: adds favorites, watch_later, watch_history tables
            // Migration 2→3: adds sourceLabel, isStreamable, uploadDate to videos table
            .addMigrations(HasikitDatabase.MIGRATION_1_2, HasikitDatabase.MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideVideoDao(database: HasikitDatabase): VideoDao {
        return database.videoDao()
    }
}

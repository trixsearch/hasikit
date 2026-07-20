package com.trixsearch.hasikit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trixsearch.hasikit.data.local.dao.VideoDao
import com.trixsearch.hasikit.data.local.entities.DownloadEntity
import com.trixsearch.hasikit.data.local.entities.FavoriteEntity
import com.trixsearch.hasikit.data.local.entities.VideoEntity
import com.trixsearch.hasikit.data.local.entities.WatchHistoryEntity
import com.trixsearch.hasikit.data.local.entities.WatchLaterEntity
import com.trixsearch.hasikit.data.local.entities.WatchProgressEntity

@Database(
    entities = [
        VideoEntity::class,
        WatchProgressEntity::class,
        DownloadEntity::class,
        // Version 2: persistent library tables — no dependency on cache or download state
        FavoriteEntity::class,
        WatchLaterEntity::class,
        WatchHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HasikitDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao

    companion object {
        // Migration 1→2: add favorites, watch_later, watch_history tables
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `favorites` (" +
                        "`videoId` TEXT NOT NULL PRIMARY KEY, " +
                        "`title` TEXT NOT NULL, " +
                        "`thumbnail` TEXT, " +
                        "`source` TEXT NOT NULL, " +
                        "`addedAt` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `watch_later` (" +
                        "`videoId` TEXT NOT NULL PRIMARY KEY, " +
                        "`title` TEXT NOT NULL, " +
                        "`thumbnail` TEXT, " +
                        "`source` TEXT NOT NULL, " +
                        "`addedAt` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `watch_history` (" +
                        "`videoId` TEXT NOT NULL PRIMARY KEY, " +
                        "`title` TEXT NOT NULL, " +
                        "`thumbnail` TEXT, " +
                        "`source` TEXT NOT NULL, " +
                        "`watchedAt` INTEGER NOT NULL)"
                )
            }
        }
    }
}

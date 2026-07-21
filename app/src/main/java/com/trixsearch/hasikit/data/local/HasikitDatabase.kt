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
        FavoriteEntity::class,
        WatchLaterEntity::class,
        WatchHistoryEntity::class
    ],
    // Version 3: added sourceLabel, isStreamable, uploadDate columns to videos table
    version = 3,
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

        // Migration 2→3: add sourceLabel, isStreamable, uploadDate to videos table
        // These columns enable SQL-level sorting/filtering in VideoDao (Metrolist pattern)
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add sourceLabel column — default empty string for existing rows
                db.execSQL("ALTER TABLE `videos` ADD COLUMN `sourceLabel` TEXT NOT NULL DEFAULT ''")
                // Add isStreamable column — default true (existing rows are MessageVideo)
                db.execSQL("ALTER TABLE `videos` ADD COLUMN `isStreamable` INTEGER NOT NULL DEFAULT 1")
                // Add uploadDate column — default 0 for existing rows (will be populated on next feed load)
                db.execSQL("ALTER TABLE `videos` ADD COLUMN `uploadDate` INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}

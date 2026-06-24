package com.trixsearch.hasikit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trixsearch.hasikit.data.local.dao.VideoDao
import com.trixsearch.hasikit.data.local.entities.VideoEntity
import com.trixsearch.hasikit.data.local.entities.WatchProgressEntity
import com.trixsearch.hasikit.data.local.entities.DownloadEntity

@Database(
    entities = [
        VideoEntity::class,
        WatchProgressEntity::class,
        DownloadEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HasikitDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
    // Add other DAOs later
}

package com.trixsearch.hasikit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Favorites table — persists user-favorited videos locally, independent of download state
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val thumbnail: String?,
    val source: String,
    val addedAt: Long = System.currentTimeMillis()
)

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistItem(
    @PrimaryKey val id: String,
    val channelId: String,
    val title: String,
    val description: String = "",
    val privacyStatus: String = "public",
    val itemCount: Int = 0,
    val thumbnailUrl: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

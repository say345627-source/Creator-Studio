package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoItem(
    @PrimaryKey val videoId: String,
    val channelId: String,
    val title: String,
    val description: String = "",
    val tags: String = "",
    val categoryId: String = "22",
    val privacyStatus: String = "public", // "public", "unlisted", "private"
    val publishAt: Long? = null,
    val publishedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 3,
    val isShort: Boolean = true,
    val uploadStatus: String = "processed", // "processed", "processing", "failed", "rejected"
    val thumbnailUrl: String = "",
    val views: Long = 0,
    val likes: Long = 0,
    val commentsCount: Long = 0,
    val selfDeclaredMadeForKids: Boolean = false,
    val notifySubscribers: Boolean = true,
    val etag: String = "",
    val syncedAt: Long = System.currentTimeMillis()
)

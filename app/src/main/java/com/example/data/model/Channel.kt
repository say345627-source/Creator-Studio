package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey val id: String,
    val title: String,
    val handle: String,
    val avatarUrl: String,
    val subscribersCount: Long,
    val videosCount: Int,
    val totalViews: Long,
    val description: String = "",
    val keywords: String = "",
    val country: String = "SA",
    val bannerUrl: String = "",
    val defaultLanguage: String = "ar",
    val lastUpdated: Long = System.currentTimeMillis()
)

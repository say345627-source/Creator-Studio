package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "post_items")
data class PostItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val batchId: String = "",
    val channelId: String = "",
    val imageUri: String = "",
    val videoPath: String? = null,
    val durationSeconds: Int = 3,
    val fittingMode: String = "BLURRED_BG", // "BLURRED_BG", "CENTER_CROP", "SOLID_BG"
    val motionEffect: String = "ZOOM_IN", // "NONE", "ZOOM_IN", "ZOOM_OUT", "PAN_HORIZONTAL"
    val title: String = "",
    val description: String = "",
    val tags: String = "#Shorts",
    val targetPrivacy: String = "public", // "public", "unlisted", "private"
    val madeForKids: Boolean = false,
    val notifySubscribers: Boolean = true,
    val playlistId: String? = null,
    val scheduledAtUtc: Long? = null, // null means instant publish
    val scheduleTz: String = "UTC",
    val status: String = "DRAFT", // "DRAFT", "QUEUED", "GENERATING", "READY", "UPLOADING", "SCHEDULED", "PUBLISHED", "FAILED", "CANCELLED"
    val progress: Float = 0f,
    val retryCount: Int = 0,
    val videoId: String? = null,
    val lastErrorCode: String? = null,
    val lastErrorMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

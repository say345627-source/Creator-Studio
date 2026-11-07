package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentItem(
    @PrimaryKey val id: String,
    val channelId: String,
    val videoId: String,
    val videoTitle: String,
    val authorName: String,
    val authorAvatarUrl: String = "",
    val textOriginal: String,
    val publishedAt: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val moderationStatus: String = "published", // "published", "heldForReview", "likelySpam"
    val isOwnerReply: Boolean = false,
    val hasReplies: Boolean = false,
    val parentId: String? = null,
    val ownerReplyText: String? = null,
    val needsReply: Boolean = true
)

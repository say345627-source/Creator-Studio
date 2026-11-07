package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CommentItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE channelId = :channelId ORDER BY publishedAt DESC")
    fun getCommentsByChannel(channelId: String): Flow<List<CommentItem>>

    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY publishedAt DESC")
    fun getCommentsByVideo(videoId: String): Flow<List<CommentItem>>

    @Query("SELECT * FROM comments WHERE channelId = :channelId AND needsReply = 1 AND moderationStatus = 'published' ORDER BY publishedAt DESC")
    fun getNeedsReplyComments(channelId: String): Flow<List<CommentItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(comment: CommentItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentItem>)

    @Update
    suspend fun update(comment: CommentItem)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun delete(id: String)
}

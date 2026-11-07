package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PostItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PostItemDao {
    @Query("SELECT * FROM post_items ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostItem>>

    @Query("SELECT * FROM post_items WHERE channelId = :channelId ORDER BY createdAt DESC")
    fun getPostsByChannel(channelId: String): Flow<List<PostItem>>

    @Query("SELECT * FROM post_items WHERE status = 'SCHEDULED' ORDER BY scheduledAtUtc ASC")
    fun getScheduledPosts(): Flow<List<PostItem>>

    @Query("SELECT * FROM post_items WHERE status IN ('DRAFT', 'QUEUED', 'GENERATING', 'UPLOADING', 'FAILED') ORDER BY createdAt DESC")
    fun getDraftsAndQueue(): Flow<List<PostItem>>

    @Query("SELECT * FROM post_items WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: String): PostItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(postItem: PostItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PostItem>)

    @Update
    suspend fun update(postItem: PostItem)

    @Query("DELETE FROM post_items WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM post_items WHERE status = 'PUBLISHED'")
    suspend fun clearCompleted()
}

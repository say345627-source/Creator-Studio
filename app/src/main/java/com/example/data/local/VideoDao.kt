package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VideoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos WHERE channelId = :channelId ORDER BY publishedAt DESC")
    fun getVideosByChannel(channelId: String): Flow<List<VideoItem>>

    @Query("SELECT * FROM videos WHERE videoId = :videoId LIMIT 1")
    fun getVideoById(videoId: String): Flow<VideoItem?>

    @Query("SELECT * FROM videos WHERE channelId = :channelId AND publishAt IS NOT NULL AND publishAt > :currentTime ORDER BY publishAt ASC")
    fun getScheduledVideos(channelId: String, currentTime: Long = System.currentTimeMillis()): Flow<List<VideoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(video: VideoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videos: List<VideoItem>)

    @Update
    suspend fun update(video: VideoItem)

    @Query("DELETE FROM videos WHERE videoId = :videoId")
    suspend fun delete(videoId: String)
}

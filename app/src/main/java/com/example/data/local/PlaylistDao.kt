package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PlaylistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists WHERE channelId = :channelId ORDER BY updatedAt DESC")
    fun getPlaylistsByChannel(channelId: String): Flow<List<PlaylistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(playlist: PlaylistItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlists: List<PlaylistItem>)

    @Update
    suspend fun update(playlist: PlaylistItem)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun delete(id: String)
}

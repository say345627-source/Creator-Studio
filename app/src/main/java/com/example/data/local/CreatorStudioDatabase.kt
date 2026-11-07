package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Channel
import com.example.data.model.CommentItem
import com.example.data.model.PlaylistItem
import com.example.data.model.PostItem
import com.example.data.model.VideoItem

@Database(
    entities = [
        Channel::class,
        VideoItem::class,
        CommentItem::class,
        PlaylistItem::class,
        PostItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CreatorStudioDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun postItemDao(): PostItemDao

    companion object {
        @Volatile
        private var INSTANCE: CreatorStudioDatabase? = null

        fun getDatabase(context: Context): CreatorStudioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CreatorStudioDatabase::class.java,
                    "creator_studio_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

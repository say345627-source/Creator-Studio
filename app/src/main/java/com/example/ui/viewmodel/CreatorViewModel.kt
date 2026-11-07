package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CreatorStudioDatabase
import com.example.data.model.AnalyticsSummary
import com.example.data.model.Channel
import com.example.data.model.CommentItem
import com.example.data.model.PlaylistItem
import com.example.data.model.PostItem
import com.example.data.model.QuotaInfo
import com.example.data.model.VideoItem
import com.example.data.repository.CreatorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class CreatorViewModel(application: Application) : AndroidViewModel(application) {
    private val database = CreatorStudioDatabase.getDatabase(application)
    private val repository = CreatorRepository(
        channelDao = database.channelDao(),
        videoDao = database.videoDao(),
        commentDao = database.commentDao(),
        playlistDao = database.playlistDao(),
        postItemDao = database.postItemDao(),
        scope = viewModelScope
    )

    val currentChannel: StateFlow<Channel?> = repository.currentChannel

    val channels: StateFlow<List<Channel>> = repository.allChannels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val videos: StateFlow<List<VideoItem>> = repository.currentChannel.flatMapLatest {
        repository.getVideosForCurrentChannel()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val comments: StateFlow<List<CommentItem>> = repository.currentChannel.flatMapLatest {
        repository.getCommentsForCurrentChannel()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val playlists: StateFlow<List<PlaylistItem>> = repository.currentChannel.flatMapLatest {
        repository.getPlaylistsForCurrentChannel()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val scheduledPosts: StateFlow<List<PostItem>> = repository.scheduledPosts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val draftsAndQueue: StateFlow<List<PostItem>> = repository.draftsAndQueue.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val quotaInfo: StateFlow<QuotaInfo> = repository.quotaInfo
    val analyticsSummary: StateFlow<AnalyticsSummary> = repository.analyticsSummary

    // UI state
    private val _selectedPeriodDays = MutableStateFlow(28)
    val selectedPeriodDays: StateFlow<Int> = _selectedPeriodDays.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _selectedVideoForEdit = MutableStateFlow<VideoItem?>(null)
    val selectedVideoForEdit: StateFlow<VideoItem?> = _selectedVideoForEdit.asStateFlow()

    fun switchChannel(channel: Channel) {
        repository.switchChannel(channel)
    }

    fun setAnalyticsPeriod(days: Int) {
        _selectedPeriodDays.value = days
        repository.setAnalyticsPeriod(days)
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            kotlinx.coroutines.delay(1000)
            _isRefreshing.value = false
        }
    }

    fun selectVideoForEdit(video: VideoItem?) {
        _selectedVideoForEdit.value = video
    }

    fun updateVideo(video: VideoItem) {
        repository.updateVideo(video)
        _selectedVideoForEdit.value = null
    }

    fun deleteVideo(videoId: String) {
        repository.deleteVideo(videoId)
        _selectedVideoForEdit.value = null
    }

    fun replyToComment(commentId: String, replyText: String) {
        repository.replyToComment(commentId, replyText)
    }

    fun setCommentModeration(commentId: String, newStatus: String) {
        repository.setCommentModeration(commentId, newStatus)
    }

    fun deleteComment(commentId: String) {
        repository.deleteComment(commentId)
    }

    fun createPlaylist(title: String, description: String, privacy: String) {
        repository.createPlaylist(title, description, privacy)
    }

    fun updateChannel(title: String, description: String, keywords: String, country: String) {
        repository.updateChannelDetails(title, description, keywords, country)
    }

    fun retryPost(item: PostItem) {
        repository.retryPost(item)
    }

    fun cancelPost(postId: String) {
        repository.cancelPost(postId)
    }

    /**
     * Submit batch of Shorts from Photos (the signature feature)
     */
    fun submitShortsBatch(
        images: List<String>,
        defaultDurationSec: Int,
        fitMode: String,
        motionEffect: String,
        titleTemplate: String,
        description: String,
        targetPrivacy: String,
        madeForKids: Boolean,
        isScheduled: Boolean,
        scheduleOffsetsHours: List<Int>
    ) {
        val channelId = currentChannel.value?.id ?: "UC_CREATOR_STUDIO_ALPHA"
        val batchId = "batch_" + UUID.randomUUID().toString().take(6)
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM", Locale("ar"))

        val postItems = images.mapIndexed { index, imageUri ->
            val scheduleTime = if (isScheduled) {
                val offset = scheduleOffsetsHours.getOrElse(index) { (index + 1) * 4 }
                now + (offset.toLong() * 3600000L)
            } else null

            val generatedTitle = if (titleTemplate.contains("{الرقم}") || titleTemplate.contains("{التاريخ}")) {
                titleTemplate
                    .replace("{الرقم}", (index + 1).toString())
                    .replace("{التاريخ}", dateFormat.format(Calendar.getInstance().time))
            } else {
                "${titleTemplate.ifBlank { "Short إبداعي" }} #${index + 1} #Shorts"
            }

            PostItem(
                id = UUID.randomUUID().toString(),
                batchId = batchId,
                channelId = channelId,
                imageUri = imageUri,
                durationSeconds = defaultDurationSec,
                fittingMode = fitMode,
                motionEffect = motionEffect,
                title = generatedTitle,
                description = description.ifBlank { "تم الإنشاء والنشر عبر Creator Studio #Shorts" },
                targetPrivacy = targetPrivacy,
                madeForKids = madeForKids,
                scheduledAtUtc = scheduleTime,
                status = "QUEUED",
                progress = 0f
            )
        }

        repository.createShortsBatch(postItems, isScheduled)
    }
}

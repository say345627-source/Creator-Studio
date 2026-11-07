package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StudioBottomBar
import com.example.ui.components.StudioTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ChannelInfoScreen
import com.example.ui.screens.CommentsScreen
import com.example.ui.screens.ContentScreen
import com.example.ui.screens.CreateShortsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.screens.PublishingQueueScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VideoDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CreatorViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: CreatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CreatorStudioApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CreatorStudioApp(viewModel: CreatorViewModel) {
    var currentRoute by remember { mutableStateOf(Screen.Dashboard.route) }

    // Collect states with lifecycle
    val currentChannel by viewModel.currentChannel.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val scheduledPosts by viewModel.scheduledPosts.collectAsStateWithLifecycle()
    val draftsAndQueue by viewModel.draftsAndQueue.collectAsStateWithLifecycle()
    val quotaInfo by viewModel.quotaInfo.collectAsStateWithLifecycle()
    val analytics by viewModel.analyticsSummary.collectAsStateWithLifecycle()
    val selectedPeriodDays by viewModel.selectedPeriodDays.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val selectedVideoForEdit by viewModel.selectedVideoForEdit.collectAsStateWithLifecycle()

    val pendingCommentsCount = comments.count { it.needsReply && it.moderationStatus == "published" }

    val isTopLevelScreen = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Content.route,
        Screen.CreateShorts.route,
        Screen.Comments.route,
        Screen.Analytics.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (isTopLevelScreen) {
                StudioTopBar(
                    currentChannel = currentChannel,
                    channels = channels,
                    quotaInfo = quotaInfo,
                    scheduledCount = scheduledPosts.size,
                    isRefreshing = isRefreshing,
                    onSwitchChannel = { viewModel.switchChannel(it) },
                    onRefresh = { viewModel.refreshData() },
                    onOpenQueue = { currentRoute = Screen.PublishingQueue.route }
                )
            }
        },
        bottomBar = {
            if (isTopLevelScreen) {
                StudioBottomBar(
                    currentRoute = currentRoute,
                    pendingCommentsCount = pendingCommentsCount,
                    onNavigate = { currentRoute = it }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { route ->
                when (route) {
                    Screen.Dashboard.route -> {
                        DashboardScreen(
                            currentChannel = currentChannel,
                            analytics = analytics,
                            videos = videos,
                            comments = comments,
                            scheduledPosts = scheduledPosts,
                            quotaInfo = quotaInfo,
                            selectedPeriodDays = selectedPeriodDays,
                            onPeriodSelected = { viewModel.setAnalyticsPeriod(it) },
                            onNavigate = { currentRoute = it },
                            onVideoClick = { video ->
                                viewModel.selectVideoForEdit(video)
                                currentRoute = Screen.VideoDetail.route
                            }
                        )
                    }

                    Screen.Content.route -> {
                        ContentScreen(
                            videos = videos,
                            scheduledPosts = scheduledPosts,
                            draftsAndQueue = draftsAndQueue,
                            onVideoClick = { video ->
                                viewModel.selectVideoForEdit(video)
                                currentRoute = Screen.VideoDetail.route
                            },
                            onCreateShortClick = { currentRoute = Screen.CreateShorts.route }
                        )
                    }

                    Screen.CreateShorts.route -> {
                        CreateShortsScreen(
                            onBatchSubmitted = { images, duration, fitMode, motion, titleTpl, desc, privacy, madeForKids, isScheduled, offsets ->
                                viewModel.submitShortsBatch(
                                    images = images,
                                    defaultDurationSec = duration,
                                    fitMode = fitMode,
                                    motionEffect = motion,
                                    titleTemplate = titleTpl,
                                    description = desc,
                                    targetPrivacy = privacy,
                                    madeForKids = madeForKids,
                                    isScheduled = isScheduled,
                                    scheduleOffsetsHours = offsets
                                )
                            },
                            onNavigateToQueue = { currentRoute = Screen.PublishingQueue.route }
                        )
                    }

                    Screen.Comments.route -> {
                        CommentsScreen(
                            comments = comments,
                            onReply = { commentId, text -> viewModel.replyToComment(commentId, text) },
                            onModerate = { commentId, status -> viewModel.setCommentModeration(commentId, status) },
                            onDelete = { commentId -> viewModel.deleteComment(commentId) }
                        )
                    }

                    Screen.Analytics.route -> {
                        AnalyticsScreen(
                            analytics = analytics,
                            videos = videos,
                            selectedPeriodDays = selectedPeriodDays,
                            onPeriodSelected = { viewModel.setAnalyticsPeriod(it) }
                        )
                    }

                    Screen.PublishingQueue.route -> {
                        PublishingQueueScreen(
                            quotaInfo = quotaInfo,
                            scheduledPosts = scheduledPosts,
                            draftsAndQueue = draftsAndQueue,
                            onBack = { currentRoute = Screen.Dashboard.route },
                            onRetryPost = { viewModel.retryPost(it) },
                            onCancelPost = { viewModel.cancelPost(it) }
                        )
                    }

                    Screen.Playlists.route -> {
                        PlaylistsScreen(
                            playlists = playlists,
                            onBack = { currentRoute = Screen.Dashboard.route },
                            onCreatePlaylist = { title, desc, privacy ->
                                viewModel.createPlaylist(title, desc, privacy)
                            }
                        )
                    }

                    Screen.ChannelInfo.route -> {
                        ChannelInfoScreen(
                            channel = currentChannel,
                            onBack = { currentRoute = Screen.Dashboard.route },
                            onSave = { title, desc, kw, country ->
                                viewModel.updateChannel(title, desc, kw, country)
                            }
                        )
                    }

                    Screen.Settings.route -> {
                        SettingsScreen(
                            currentChannel = currentChannel,
                            onBack = { currentRoute = Screen.Dashboard.route }
                        )
                    }

                    Screen.VideoDetail.route -> {
                        val videoToEdit = selectedVideoForEdit
                        if (videoToEdit != null) {
                            VideoDetailScreen(
                                video = videoToEdit,
                                onBack = { currentRoute = Screen.Content.route },
                                onSave = { updatedVideo ->
                                    viewModel.updateVideo(updatedVideo)
                                    currentRoute = Screen.Content.route
                                },
                                onDelete = { videoId ->
                                    viewModel.deleteVideo(videoId)
                                    currentRoute = Screen.Content.route
                                }
                            )
                        } else {
                            currentRoute = Screen.Content.route
                        }
                    }

                    else -> {
                        DashboardScreen(
                            currentChannel = currentChannel,
                            analytics = analytics,
                            videos = videos,
                            comments = comments,
                            scheduledPosts = scheduledPosts,
                            quotaInfo = quotaInfo,
                            selectedPeriodDays = selectedPeriodDays,
                            onPeriodSelected = { viewModel.setAnalyticsPeriod(it) },
                            onNavigate = { currentRoute = it },
                            onVideoClick = { video ->
                                viewModel.selectVideoForEdit(video)
                                currentRoute = Screen.VideoDetail.route
                            }
                        )
                    }
                }
            }
        }
    }
}

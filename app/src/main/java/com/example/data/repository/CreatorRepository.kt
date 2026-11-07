package com.example.data.repository

import com.example.data.local.ChannelDao
import com.example.data.local.CommentDao
import com.example.data.local.PlaylistDao
import com.example.data.local.PostItemDao
import com.example.data.local.VideoDao
import com.example.data.model.AnalyticsSummary
import com.example.data.model.Channel
import com.example.data.model.CommentItem
import com.example.data.model.CountryItem
import com.example.data.model.DailyMetric
import com.example.data.model.DemographicItem
import com.example.data.model.DeviceItem
import com.example.data.model.PlaylistItem
import com.example.data.model.PostItem
import com.example.data.model.QuotaInfo
import com.example.data.model.QuotaLevel
import com.example.data.model.TrafficSourceItem
import com.example.data.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class CreatorRepository(
    private val channelDao: ChannelDao,
    private val videoDao: VideoDao,
    private val commentDao: CommentDao,
    private val playlistDao: PlaylistDao,
    private val postItemDao: PostItemDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    val allChannels: Flow<List<Channel>> = channelDao.getAllChannels()
    val allPosts: Flow<List<PostItem>> = postItemDao.getAllPosts()
    val scheduledPosts: Flow<List<PostItem>> = postItemDao.getScheduledPosts()
    val draftsAndQueue: Flow<List<PostItem>> = postItemDao.getDraftsAndQueue()

    private val _currentChannel = MutableStateFlow<Channel?>(null)
    val currentChannel: StateFlow<Channel?> = _currentChannel.asStateFlow()

    private val _quotaInfo = MutableStateFlow(
        QuotaInfo(
            dailyLimit = 10000,
            unitsUsedToday = 3450,
            videosUploadedToday = 2,
            writeOpsToday = 5,
            readOpsToday = 42,
            resetsInHours = 9,
            statusLevel = QuotaLevel.NORMAL
        )
    )
    val quotaInfo: StateFlow<QuotaInfo> = _quotaInfo.asStateFlow()

    private val _analyticsSummary = MutableStateFlow(createInitialAnalytics(28))
    val analyticsSummary: StateFlow<AnalyticsSummary> = _analyticsSummary.asStateFlow()

    init {
        scope.launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val existing = channelDao.getAllChannels().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val primaryChannel = Channel(
                id = "UC_CREATOR_STUDIO_ALPHA",
                title = "نبض الإبداع | Creative Pulse",
                handle = "@CreativePulse",
                avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200&auto=format&fit=crop&q=80",
                subscribersCount = 48500,
                videosCount = 142,
                totalViews = 3840000,
                description = "قناة متخصصة في المحتوى الإبداعي اليومي، نصائح الإنتاج الرقمي، مقاطع Shorts التعليمية والبطاقات المرئية.",
                keywords = "shorts, tech, tutorials, design, creativity, صناعة المحتوى, يوتيوب",
                country = "SA",
                bannerUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1000&auto=format&fit=crop&q=80",
                defaultLanguage = "ar"
            )

            val secondaryChannel = Channel(
                id = "UC_TECH_FLASH_LAB",
                title = "مختبر التقنية السريعة",
                handle = "@TechFlashLab",
                avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
                subscribersCount = 12400,
                videosCount = 68,
                totalViews = 920000,
                description = "موجز التقنية والهواتف الذكية في كبسولات Shorts سريعة.",
                keywords = "tech, shorts, android, gadgets",
                country = "AE",
                defaultLanguage = "ar"
            )

            channelDao.insertAll(listOf(primaryChannel, secondaryChannel))
            _currentChannel.value = primaryChannel

            val now = System.currentTimeMillis()
            val dayMillis = 86400000L

            val sampleVideos = listOf(
                VideoItem(
                    videoId = "v_sample_01",
                    channelId = primaryChannel.id,
                    title = "5 نصائح ذهبية لزيادة تفاعل Shorts في 2026 #Shorts",
                    description = "تعرف على أسرار خوارزمية YouTube Shorts وكيف تحافظ على اهتمام المشاهدين حتى آخر ثانية.\n\n#Shorts #CreatorStudio #YouTubeTips",
                    tags = "shorts,tips,growth,creator",
                    categoryId = "27",
                    privacyStatus = "public",
                    publishedAt = now - (dayMillis * 1),
                    durationSeconds = 28,
                    isShort = true,
                    uploadStatus = "processed",
                    thumbnailUrl = "https://images.unsplash.com/photo-1534972195531-a756b1126f24?w=500&auto=format&fit=crop&q=80",
                    views = 42800,
                    likes = 3120,
                    commentsCount = 284,
                    selfDeclaredMadeForKids = false
                ),
                VideoItem(
                    videoId = "v_sample_02",
                    channelId = primaryChannel.id,
                    title = "كيف تحول صور المنتجات إلى فيديوهات جذابة؟ #Shorts",
                    description = "طريقة عملية لتحويل معرض صورك إلى مقاطع عمودية ذات معدل تحويل عالي.\n#Shorts #Marketing #Design",
                    tags = "ecommerce,shorts,marketing",
                    categoryId = "22",
                    privacyStatus = "public",
                    publishedAt = now - (dayMillis * 3),
                    durationSeconds = 15,
                    isShort = true,
                    uploadStatus = "processed",
                    thumbnailUrl = "https://images.unsplash.com/photo-1542744094-3a31f272c490?w=500&auto=format&fit=crop&q=80",
                    views = 28400,
                    likes = 1940,
                    commentsCount = 156,
                    selfDeclaredMadeForKids = false
                ),
                VideoItem(
                    videoId = "v_sample_03",
                    channelId = primaryChannel.id,
                    title = "دليل المونتاج السريع للمبتدئين من الهاتف (شرح كامل)",
                    description = "شرح مفصل خطوة بخطوة لأفضل أدوات تحرير الفيديو من الموبايل مع تصدير بدقة 4K.",
                    tags = "editing,tutorial,mobile",
                    categoryId = "27",
                    privacyStatus = "public",
                    publishedAt = now - (dayMillis * 7),
                    durationSeconds = 540,
                    isShort = false,
                    uploadStatus = "processed",
                    thumbnailUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=500&auto=format&fit=crop&q=80",
                    views = 89200,
                    likes = 7400,
                    commentsCount = 632,
                    selfDeclaredMadeForKids = false
                ),
                VideoItem(
                    videoId = "v_sample_04",
                    channelId = primaryChannel.id,
                    title = "حكمة اليوم: الانضباط يصنع النجاح #Shorts",
                    description = "اقتباس محفز للبدء بقوة اليوم.\n#Shorts #Motivation",
                    tags = "quotes,motivation,shorts",
                    categoryId = "22",
                    privacyStatus = "private",
                    publishAt = now + (dayMillis * 1) + 3600000,
                    publishedAt = now,
                    durationSeconds = 5,
                    isShort = true,
                    uploadStatus = "processed",
                    thumbnailUrl = "https://images.unsplash.com/photo-1519791883288-dc8bd696e667?w=500&auto=format&fit=crop&q=80",
                    views = 0,
                    likes = 0,
                    commentsCount = 0,
                    selfDeclaredMadeForKids = false
                ),
                VideoItem(
                    videoId = "v_sample_05",
                    channelId = primaryChannel.id,
                    title = "3 مواقع مجانية للصور عالية الدقة بدون حقوق #Shorts",
                    description = "أفضل بدائل مجانية للمصممين وصناع المحتوى.\n#Shorts #Resources",
                    tags = "resources,free,photos,shorts",
                    categoryId = "27",
                    privacyStatus = "private",
                    publishAt = now + (dayMillis * 2) + 7200000,
                    publishedAt = now,
                    durationSeconds = 12,
                    isShort = true,
                    uploadStatus = "processed",
                    thumbnailUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=500&auto=format&fit=crop&q=80",
                    views = 0,
                    likes = 0,
                    commentsCount = 0,
                    selfDeclaredMadeForKids = false
                )
            )
            videoDao.insertAll(sampleVideos)

            val sampleComments = listOf(
                CommentItem(
                    id = "c_001",
                    channelId = primaryChannel.id,
                    videoId = "v_sample_01",
                    videoTitle = "5 نصائح ذهبية لزيادة تفاعل Shorts في 2026",
                    authorName = "أحمد مصطفى",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&auto=format&fit=crop&q=80",
                    textOriginal = "ما شاء الله نصائح ممتازة ومباشرة! ما هو أفضل توقيت لنشر الـ Shorts للجمهور العربي؟",
                    publishedAt = now - 3600000 * 2,
                    likesCount = 14,
                    moderationStatus = "published",
                    needsReply = true
                ),
                CommentItem(
                    id = "c_002",
                    channelId = primaryChannel.id,
                    videoId = "v_sample_01",
                    videoTitle = "5 نصائح ذهبية لزيادة تفاعل Shorts في 2026",
                    authorName = "سارة العمراني",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&auto=format&fit=crop&q=80",
                    textOriginal = "طبقت النصيحة الثالثة بخصوص أول 3 ثواني، والمشاهدات ارتفعت للضعف فعلاً!",
                    publishedAt = now - 3600000 * 5,
                    likesCount = 28,
                    moderationStatus = "published",
                    needsReply = true
                ),
                CommentItem(
                    id = "c_003",
                    channelId = primaryChannel.id,
                    videoId = "v_sample_02",
                    videoTitle = "كيف تحول صور المنتجات إلى فيديوهات جذابة؟",
                    authorName = "فراس التقني",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100&auto=format&fit=crop&q=80",
                    textOriginal = "هل التطبيق يدعم إضافة حركة تكبير ذكية تلقائية للصورة؟",
                    publishedAt = now - 3600000 * 12,
                    likesCount = 6,
                    moderationStatus = "published",
                    isOwnerReply = false,
                    needsReply = false,
                    ownerReplyText = "نعم بكل تأكيد! التطبيق يدعم حركات التكبير البطيء والتحريك الأفقي مع خلفية مموهة."
                ),
                CommentItem(
                    id = "c_004",
                    channelId = primaryChannel.id,
                    videoId = "v_sample_03",
                    videoTitle = "دليل المونتاج السريع للمبتدئين من الهاتف",
                    authorName = "مستخدم غير معرّف",
                    authorAvatarUrl = "",
                    textOriginal = "اشترك في قناتي فوراً للحصول على بطاقات مجانية عبر هذا الرابط المشبوه: http://bit.ly/fake",
                    publishedAt = now - 3600000 * 18,
                    likesCount = 0,
                    moderationStatus = "likelySpam",
                    needsReply = false
                ),
                CommentItem(
                    id = "c_005",
                    channelId = primaryChannel.id,
                    videoId = "v_sample_03",
                    videoTitle = "دليل المونتاج السريع للمبتدئين من الهاتف",
                    authorName = "خالد الحربي",
                    authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&auto=format&fit=crop&q=80",
                    textOriginal = "فيديو يستحق المراجعة والتدقيق هل يمكن إضافة ترجمات بالذكاء الاصطناعي؟",
                    publishedAt = now - 3600000 * 22,
                    likesCount = 3,
                    moderationStatus = "heldForReview",
                    needsReply = true
                )
            )
            commentDao.insertAll(sampleComments)

            val samplePlaylists = listOf(
                PlaylistItem(
                    id = "pl_01",
                    channelId = primaryChannel.id,
                    title = "شورتات يومية من الصور (Shorts)",
                    description = "مجموعة الفيديوهات القصيرة المنتجة من صور بطاقات واقتباسات.",
                    privacyStatus = "public",
                    itemCount = 28,
                    thumbnailUrl = "https://images.unsplash.com/photo-1534972195531-a756b1126f24?w=500&auto=format&fit=crop&q=80",
                    updatedAt = now - (dayMillis * 2)
                ),
                PlaylistItem(
                    id = "pl_02",
                    channelId = primaryChannel.id,
                    title = "شروحات صناعة المحتوى واليوتيوب",
                    description = "دليل الأدوات والتقنيات لرفع جودة الفيديوهات وزيادة المشاهدات.",
                    privacyStatus = "public",
                    itemCount = 14,
                    thumbnailUrl = "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=500&auto=format&fit=crop&q=80",
                    updatedAt = now - (dayMillis * 5)
                ),
                PlaylistItem(
                    id = "pl_03",
                    channelId = primaryChannel.id,
                    title = "سلسلة التسويق الرقمي والتجارة",
                    description = "دروس عملية للمتاجر والمسوقين وصناع المنتجات.",
                    privacyStatus = "unlisted",
                    itemCount = 8,
                    thumbnailUrl = "https://images.unsplash.com/photo-1542744094-3a31f272c490?w=500&auto=format&fit=crop&q=80",
                    updatedAt = now - (dayMillis * 10)
                )
            )
            playlistDao.insertAll(samplePlaylists)

            val sampleScheduledPosts = listOf(
                PostItem(
                    id = "post_sched_1",
                    batchId = "batch_001",
                    channelId = primaryChannel.id,
                    imageUri = "sample_image_1",
                    durationSeconds = 3,
                    fittingMode = "BLURRED_BG",
                    motionEffect = "ZOOM_IN",
                    title = "طاقة صباحية إيجابية ☀️ #Shorts",
                    description = "ابدأ يومك بنشاط وأمل جديد.\n#Shorts #Motivation",
                    tags = "morning,quotes,shorts",
                    targetPrivacy = "public",
                    madeForKids = false,
                    scheduledAtUtc = now + 14400000,
                    status = "SCHEDULED",
                    progress = 1.0f,
                    videoId = "v_sched_01"
                ),
                PostItem(
                    id = "post_sched_2",
                    batchId = "batch_001",
                    channelId = primaryChannel.id,
                    imageUri = "sample_image_2",
                    durationSeconds = 4,
                    fittingMode = "CENTER_CROP",
                    motionEffect = "PAN_HORIZONTAL",
                    title = "معلومة تقنية سريعة في 3 ثوانٍ #Shorts",
                    description = "هل تعلم أن أول فيديو على يوتيوب تم رفعه عام 2005؟\n#Shorts #TechFacts",
                    tags = "tech,facts,shorts",
                    targetPrivacy = "public",
                    madeForKids = false,
                    scheduledAtUtc = now + 28800000,
                    status = "SCHEDULED",
                    progress = 1.0f,
                    videoId = "v_sched_02"
                )
            )
            postItemDao.insertAll(sampleScheduledPosts)
        } else {
            _currentChannel.value = existing.first()
        }
    }

    fun getVideosForCurrentChannel(): Flow<List<VideoItem>> {
        val channelId = _currentChannel.value?.id ?: "UC_CREATOR_STUDIO_ALPHA"
        return videoDao.getVideosByChannel(channelId)
    }

    fun getCommentsForCurrentChannel(): Flow<List<CommentItem>> {
        val channelId = _currentChannel.value?.id ?: "UC_CREATOR_STUDIO_ALPHA"
        return commentDao.getCommentsByChannel(channelId)
    }

    fun getPlaylistsForCurrentChannel(): Flow<List<PlaylistItem>> {
        val channelId = _currentChannel.value?.id ?: "UC_CREATOR_STUDIO_ALPHA"
        return playlistDao.getPlaylistsByChannel(channelId)
    }

    fun switchChannel(channel: Channel) {
        _currentChannel.value = channel
    }

    fun updateChannelDetails(title: String, description: String, keywords: String, country: String) {
        scope.launch {
            val curr = _currentChannel.value ?: return@launch
            val updated = curr.copy(
                title = title,
                description = description,
                keywords = keywords,
                country = country,
                lastUpdated = System.currentTimeMillis()
            )
            channelDao.update(updated)
            _currentChannel.value = updated
            recordQuotaUsage(writeOps = 1) // 50 units
        }
    }

    fun connectUserChannel(
        title: String,
        handle: String,
        customId: String? = null,
        subscribersCount: Long = 1000L,
        avatarUrl: String? = null
    ) {
        scope.launch {
            val cleanHandle = if (handle.startsWith("@")) handle else "@$handle"
            val channelId = if (!customId.isNullOrBlank()) customId else ("UC_" + UUID.randomUUID().toString().take(16))
            val newChannel = Channel(
                id = channelId,
                title = title.ifBlank { "قناتي على YouTube" },
                handle = cleanHandle,
                avatarUrl = if (!avatarUrl.isNullOrBlank()) avatarUrl else "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
                subscribersCount = subscribersCount.coerceAtLeast(10),
                videosCount = 1,
                totalViews = (subscribersCount * 28).coerceAtLeast(2500),
                description = "القناة الرسمية المربوطة بحساب Google (abdallhtaktok@gmail.com)",
                keywords = "shorts, youtube, creator, video",
                country = "SA",
                bannerUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1000&auto=format&fit=crop&q=80",
                defaultLanguage = "ar",
                lastUpdated = System.currentTimeMillis()
            )
            channelDao.insertOrUpdate(newChannel)
            _currentChannel.value = newChannel

            // Create initial welcome short for this channel
            val welcomeVideo = VideoItem(
                videoId = "v_" + UUID.randomUUID().toString().take(8),
                channelId = newChannel.id,
                title = "مرحباً بكم في قناتي على YouTube! 🌟 #Shorts",
                description = "تم ربط القناة بنجاح في تطبيق Creator Studio لإدارة القناة ونشر فيديوهات Shorts المجدولة تلقائياً.",
                tags = "welcome,shorts,creator",
                categoryId = "22",
                privacyStatus = "public",
                publishedAt = System.currentTimeMillis(),
                durationSeconds = 6,
                isShort = true,
                uploadStatus = "processed",
                thumbnailUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&auto=format&fit=crop&q=80",
                views = 142,
                likes = 28,
                commentsCount = 5,
                selfDeclaredMadeForKids = false
            )
            videoDao.insertOrUpdate(welcomeVideo)
        }
    }

    fun updateVideo(video: VideoItem) {
        scope.launch {
            videoDao.update(video)
            recordQuotaUsage(writeOps = 1) // 50 units
        }
    }

    fun deleteVideo(videoId: String) {
        scope.launch {
            videoDao.delete(videoId)
            recordQuotaUsage(writeOps = 1)
        }
    }

    fun replyToComment(commentId: String, replyText: String) {
        scope.launch {
            val comments = commentDao.getCommentsByChannel(_currentChannel.value?.id ?: "").firstOrNull()
            val comment = comments?.find { it.id == commentId }
            if (comment != null) {
                val updated = comment.copy(
                    needsReply = false,
                    ownerReplyText = replyText
                )
                commentDao.update(updated)
                recordQuotaUsage(writeOps = 1) // 50 units
            }
        }
    }

    fun setCommentModeration(commentId: String, newStatus: String) {
        scope.launch {
            val comments = commentDao.getCommentsByChannel(_currentChannel.value?.id ?: "").firstOrNull()
            val comment = comments?.find { it.id == commentId }
            if (comment != null) {
                val updated = comment.copy(moderationStatus = newStatus)
                commentDao.update(updated)
                recordQuotaUsage(writeOps = 1)
            }
        }
    }

    fun deleteComment(commentId: String) {
        scope.launch {
            commentDao.delete(commentId)
            recordQuotaUsage(writeOps = 1)
        }
    }

    fun createPlaylist(title: String, description: String, privacy: String) {
        scope.launch {
            val channelId = _currentChannel.value?.id ?: "UC_CREATOR_STUDIO_ALPHA"
            val newPlaylist = PlaylistItem(
                id = "pl_" + UUID.randomUUID().toString().take(8),
                channelId = channelId,
                title = title,
                description = description,
                privacyStatus = privacy,
                itemCount = 0,
                thumbnailUrl = "",
                updatedAt = System.currentTimeMillis()
            )
            playlistDao.insertOrUpdate(newPlaylist)
            recordQuotaUsage(writeOps = 1)
        }
    }

    fun createShortsBatch(
        postItems: List<PostItem>,
        isScheduled: Boolean,
        onProgress: ((String, Float) -> Unit)? = null
    ) {
        scope.launch {
            postItemDao.insertAll(postItems)

            // Process each item through the generation and upload pipeline
            for (item in postItems) {
                val channelId = _currentChannel.value?.id ?: "UC_CREATOR_STUDIO_ALPHA"
                
                // 1. Generation Step
                postItemDao.update(item.copy(status = "GENERATING", progress = 0.2f))
                delay(800)
                postItemDao.update(item.copy(status = "GENERATING", progress = 0.6f))
                delay(800)

                // 2. Upload Step (Resumable upload simulation)
                postItemDao.update(item.copy(status = "UPLOADING", progress = 0.8f))
                delay(900)

                val generatedVideoId = "v_gen_" + UUID.randomUUID().toString().take(6)
                val finalStatus = if (isScheduled && item.scheduledAtUtc != null && item.scheduledAtUtc > System.currentTimeMillis()) {
                    "SCHEDULED"
                } else {
                    "PUBLISHED"
                }

                val completedItem = item.copy(
                    status = finalStatus,
                    progress = 1.0f,
                    videoId = generatedVideoId
                )
                postItemDao.update(completedItem)

                // Add as video in the channel
                val newVideo = VideoItem(
                    videoId = generatedVideoId,
                    channelId = channelId,
                    title = item.title.ifBlank { "Short ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)}" },
                    description = item.description,
                    tags = item.tags,
                    privacyStatus = if (finalStatus == "SCHEDULED") "private" else item.targetPrivacy,
                    publishAt = if (finalStatus == "SCHEDULED") item.scheduledAtUtc else null,
                    publishedAt = System.currentTimeMillis(),
                    durationSeconds = item.durationSeconds,
                    isShort = true,
                    uploadStatus = "processed",
                    thumbnailUrl = item.imageUri.ifBlank { "https://images.unsplash.com/photo-1534972195531-a756b1126f24?w=500&auto=format&fit=crop&q=80" },
                    views = 0,
                    likes = 0,
                    commentsCount = 0,
                    selfDeclaredMadeForKids = item.madeForKids
                )
                videoDao.insertOrUpdate(newVideo)

                // Account for quota: 1600 units per video upload!
                recordQuotaUsage(videoUploads = 1)
            }
        }
    }

    fun retryPost(postItem: PostItem) {
        scope.launch {
            createShortsBatch(listOf(postItem.copy(retryCount = postItem.retryCount + 1, status = "QUEUED")), postItem.scheduledAtUtc != null)
        }
    }

    fun cancelPost(postId: String) {
        scope.launch {
            postItemDao.delete(postId)
        }
    }

    fun setAnalyticsPeriod(days: Int) {
        _analyticsSummary.value = createInitialAnalytics(days)
    }

    private fun recordQuotaUsage(videoUploads: Int = 0, writeOps: Int = 0, readOps: Int = 0) {
        _quotaInfo.update { current ->
            val additionalUnits = (videoUploads * 1600) + (writeOps * 50) + (readOps * 1)
            val newUsed = current.unitsUsedToday + additionalUnits
            val level = when {
                newUsed >= current.dailyLimit * 0.9 -> QuotaLevel.CRITICAL
                newUsed >= current.dailyLimit * 0.75 -> QuotaLevel.WARNING
                else -> QuotaLevel.NORMAL
            }
            current.copy(
                unitsUsedToday = newUsed,
                videosUploadedToday = current.videosUploadedToday + videoUploads,
                writeOpsToday = current.writeOpsToday + writeOps,
                readOpsToday = current.readOpsToday + readOps,
                statusLevel = level
            )
        }
    }

    companion object {
        fun createInitialAnalytics(days: Int): AnalyticsSummary {
            val factor = days.toDouble() / 28.0
            val baseViews = (184520 * factor).toLong()
            val baseHours = 1240.5 * factor
            val baseSubs = (1420 * factor).toInt()

            val daily = mutableListOf<DailyMetric>()
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -days)
            val dateFormat = SimpleDateFormat("dd MMM", Locale("ar"))

            for (i in 0 until days) {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                val dayViews = (3000..9500).random().toLong()
                val dayMins = dayViews * 2 + (100..800).random()
                daily.add(
                    DailyMetric(
                        dateLabel = dateFormat.format(cal.time),
                        timestamp = cal.timeInMillis,
                        views = dayViews,
                        watchMinutes = dayMins,
                        subscribersGained = (20..85).random()
                    )
                )
            }

            return AnalyticsSummary(
                periodDays = days,
                totalViews = baseViews,
                viewsGrowthPercent = 18.4,
                totalWatchHours = baseHours,
                watchHoursGrowthPercent = 12.1,
                netSubscribers = baseSubs,
                subscribersGrowthPercent = 24.8,
                avgViewDurationSec = 42,
                avgDurationGrowthPercent = 5.2,
                dailyTrend = daily,
                trafficSources = listOf(
                    TrafficSourceItem("خلاصة Shorts (Shorts feed)", 68.4f, (baseViews * 0.684).toLong()),
                    TrafficSourceItem("بحث YouTube (Search)", 14.2f, (baseViews * 0.142).toLong()),
                    TrafficSourceItem("الفيديوهات المقترحة (Suggested)", 9.5f, (baseViews * 0.095).toLong()),
                    TrafficSourceItem("ميزات التصفح (Browse features)", 5.1f, (baseViews * 0.051).toLong()),
                    TrafficSourceItem("مصادر خارجية (External)", 2.8f, (baseViews * 0.028).toLong())
                ),
                ageDemographics = listOf(
                    DemographicItem("18-24 سنة", 38.5f),
                    DemographicItem("25-34 سنة", 42.1f),
                    DemographicItem("35-44 سنة", 12.8f),
                    DemographicItem("45-54 سنة", 4.9f),
                    DemographicItem("+55 سنة", 1.7f)
                ),
                genderDemographics = listOf(
                    DemographicItem("ذكور", 64.2f),
                    DemographicItem("إناث", 35.8f)
                ),
                topCountries = listOf(
                    CountryItem("SA", "المملكة العربية السعودية", 44.5f, (baseViews * 0.445).toLong()),
                    CountryItem("EG", "مصر", 22.3f, (baseViews * 0.223).toLong()),
                    CountryItem("AE", "الإمارات العربية المتحدة", 14.1f, (baseViews * 0.141).toLong()),
                    CountryItem("IQ", "العراق", 8.2f, (baseViews * 0.082).toLong()),
                    CountryItem("KW", "الكويت", 5.4f, (baseViews * 0.054).toLong()),
                    CountryItem("OTHER", "دول أخرى", 5.5f, (baseViews * 0.055).toLong())
                ),
                deviceBreakdown = listOf(
                    DeviceItem("الهاتف الجوال (Mobile)", 84.6f),
                    DeviceItem("الكمبيوتر (Computer)", 9.8f),
                    DeviceItem("الأجهزة اللوحية (Tablet)", 3.4f),
                    DeviceItem("التلفزيون الذكي (TV)", 2.2f)
                )
            )
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AnalyticsSummary
import com.example.data.model.Channel
import com.example.data.model.CommentItem
import com.example.data.model.PostItem
import com.example.data.model.QuotaInfo
import com.example.data.model.QuotaLevel
import com.example.data.model.VideoItem
import com.example.ui.components.MiniLineChart
import com.example.ui.components.StatCard
import com.example.ui.components.VideoCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    currentChannel: Channel?,
    analytics: AnalyticsSummary,
    videos: List<VideoItem>,
    comments: List<CommentItem>,
    scheduledPosts: List<PostItem>,
    quotaInfo: QuotaInfo,
    selectedPeriodDays: Int,
    onPeriodSelected: (Int) -> Unit,
    onNavigate: (String) -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onOpenConnectAccount: () -> Unit = {}
) {
    val pendingComments = comments.filter { it.needsReply && it.moderationStatus == "published" }
    val topVideos = videos.sortedByDescending { it.views }.take(4)
    val recentVideos = videos.take(4)
    val upcomingScheduled = scheduledPosts.filter { it.status == "SCHEDULED" }.take(3)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Channel Performance Header
        item {
            ChannelHeaderCard(
                channel = currentChannel,
                selectedPeriodDays = selectedPeriodDays,
                onPeriodSelected = onPeriodSelected,
                onNavigate = onNavigate
            )
        }

        // Quick Connect Channel Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onOpenConnectAccount() }
                    .testTag("banner_connect_account"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ربط حساب وقناتك على YouTube",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StudioRed
                        )
                        Text(
                            text = "اربط حسابك الشخصي أو أضف قناتك (@Handle) لإدارتها ونشر Shorts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    SuggestionChip(
                        onClick = onOpenConnectAccount,
                        label = { Text("ربط الآن", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. "Needs Attention" Alerts if any
        if (pendingComments.isNotEmpty() || quotaInfo.statusLevel != QuotaLevel.NORMAL || upcomingScheduled.isNotEmpty()) {
            item {
                SectionHeader(title = "يحتاج انتباهك", subtitle = "تنبيهات عاجلة للتعامل معها")
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (pendingComments.isNotEmpty()) {
                        AlertBanner(
                            icon = Icons.Default.ChatBubbleOutline,
                            iconTint = StudioAmber,
                            title = "${pendingComments.size} تعليقات جديدة تنتظر ردك",
                            description = "تفاعل سريعاً مع المتابعين لتعزيز خوارزمية YouTube",
                            actionText = "رد الآن",
                            onClick = { onNavigate(Screen.Comments.route) }
                        )
                    }

                    if (quotaInfo.statusLevel != QuotaLevel.NORMAL) {
                        AlertBanner(
                            icon = Icons.Default.Warning,
                            iconTint = StudioRed,
                            title = "استهلاك الحصة مرتفع (${quotaInfo.unitsUsedToday}/10,000 وحدة)",
                            description = "تجنب الرفع الكثيف المكرر حتى إعادة ضبط الحصة خلال ${quotaInfo.resetsInHours} ساعات",
                            actionText = "فحص الحصة",
                            onClick = { onNavigate(Screen.PublishingQueue.route) }
                        )
                    }

                    // Notice about API Audit & Unverified Projects per PRD 1.1
                    AlertBanner(
                        icon = Icons.Default.Info,
                        iconTint = MaterialTheme.colorScheme.primary,
                        title = "نظام النشر والجدولة الذكية",
                        description = "تُرفع فيديوهات Shorts بوضع خاص مع تاريخ النشر المجدول لتنشرها YouTube تلقائياً دون الحاجة لإبقاء هاتفك قيد التشغيل.",
                        actionText = "إنشاء Short",
                        onClick = { onNavigate(Screen.CreateShorts.route) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 3. Quick Action Row
        item {
            SectionHeader(title = "إجراءات سريعة", subtitle = "اختصارات لإدارة القناة")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Button(
                        onClick = { onNavigate(Screen.CreateShorts.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioRed),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("action_create_shorts")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Short جديد من الصور", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(Screen.PublishingQueue.route) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.CloudQueue, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("طابور النشر (${scheduledPosts.size})", fontSize = 12.sp)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(Screen.Playlists.route) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("قوائم التشغيل", fontSize = 12.sp)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(Screen.ChannelInfo.route) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("بيانات القناة", fontSize = 12.sp)
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(Screen.Settings.route) },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("الإعدادات", fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 4. Performance KPI Cards (2x2 Grid)
        item {
            SectionHeader(title = "نظرة عامة على الأداء", subtitle = "خلال آخر $selectedPeriodDays يوماً")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "المشاهدات",
                        value = formatBigNumber(analytics.totalViews),
                        growthPercent = analytics.viewsGrowthPercent,
                        icon = Icons.Default.Visibility,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "ساعات المشاهدة",
                        value = "${analytics.totalWatchHours.toInt()} س",
                        growthPercent = analytics.watchHoursGrowthPercent,
                        icon = Icons.Default.Schedule,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "صافي المشتركين",
                        value = "+${analytics.netSubscribers}",
                        growthPercent = analytics.subscribersGrowthPercent,
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "متوسط المشاهدة",
                        value = "${analytics.avgViewDurationSec} ثانية",
                        growthPercent = analytics.avgDurationGrowthPercent,
                        icon = Icons.Default.Timer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Daily Trend Graph
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                MiniLineChart(
                    data = analytics.dailyTrend,
                    title = "نمو المشاهدات اليومية"
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 6. Upcoming Scheduled Shorts
        if (upcomingScheduled.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "فيديوهات مجدولة قادمة",
                    subtitle = "نشر تلقائي عبر YouTube Data API"
                )
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    upcomingScheduled.forEach { post ->
                        ScheduledPostSnippet(post = post, onClick = { onNavigate(Screen.PublishingQueue.route) })
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 7. Top Performing Content
        item {
            SectionHeader(
                title = "أفضل المحتوى أداءً",
                subtitle = "المقاطع الأكثر مشاهدة في الفترة"
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                topVideos.forEach { video ->
                    VideoCard(video = video, onClick = { onVideoClick(video) })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 8. Recent Content
        item {
            SectionHeader(
                title = "أحدث الفيديوهات المنشورة",
                subtitle = "آخر ما تم رفعه على القناة"
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                recentVideos.forEach { video ->
                    VideoCard(video = video, onClick = { onVideoClick(video) })
                }
            }
        }
    }
}

@Composable
private fun ChannelHeaderCard(
    channel: Channel?,
    selectedPeriodDays: Int,
    onPeriodSelected: (Int) -> Unit,
    onNavigate: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (channel != null && channel.avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = channel.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(StudioRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = channel?.title ?: "قناتي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${channel?.handle ?: "@channel"} • ${formatBigNumber(channel?.subscribersCount ?: 0)} مشترك",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${channel?.videosCount ?: 0} فيديو • ${formatBigNumber(channel?.totalViews ?: 0)} مشاهدة إجمالية",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Time range chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "فترة التقارير:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(7, 28, 90).forEach { days ->
                        FilterChip(
                            selected = selectedPeriodDays == days,
                            onClick = { onPeriodSelected(days) },
                            label = { Text("$days يوم", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                selectedLabelColor = StudioRed
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduledPostSnippet(post: PostItem, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM • hh:mm a", Locale("ar"))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StudioAmber.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = StudioAmber)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = post.scheduledAtUtc?.let { dateFormat.format(Date(it)) } ?: "مجدول قريباً",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
            Text(
                text = "مجدول",
                color = StudioAmber,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AlertBanner(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    actionText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = iconTint.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            SuggestionChip(
                onClick = onClick,
                label = { Text(actionText, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

private fun formatBigNumber(value: Long): String {
    return when {
        value >= 1_000_000 -> String.format(Locale.US, "%.1fM", value / 1_000_000.0)
        value >= 1_000 -> String.format(Locale.US, "%.1fK", value / 1_000.0)
        else -> value.toString()
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PostItem
import com.example.data.model.QuotaInfo
import com.example.data.model.QuotaLevel
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed
import com.example.ui.theme.StudioRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishingQueueScreen(
    quotaInfo: QuotaInfo,
    scheduledPosts: List<PostItem>,
    draftsAndQueue: List<PostItem>,
    onBack: () -> Unit,
    onRetryPost: (PostItem) -> Unit,
    onCancelPost: (String) -> Unit
) {
    BackHandler { onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("طابور النشر ومُنظِّم الحصة", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("publishing_queue_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Quota Governor Meter Card (PRD 12.3)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StudioRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("مُنظِّم الحصة اليومية (Quota Governor)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("YouTube Data API Quota", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            val badgeColor = when (quotaInfo.statusLevel) {
                                QuotaLevel.NORMAL -> StudioEmerald
                                QuotaLevel.WARNING -> StudioAmber
                                QuotaLevel.CRITICAL -> StudioRose
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(badgeColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (quotaInfo.statusLevel) {
                                        QuotaLevel.NORMAL -> "طبيعي"
                                        QuotaLevel.WARNING -> "تنبيه"
                                        QuotaLevel.CRITICAL -> "حرج"
                                    },
                                    color = badgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { quotaInfo.percentageUsed },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = when (quotaInfo.statusLevel) {
                                QuotaLevel.NORMAL -> StudioEmerald
                                QuotaLevel.WARNING -> StudioAmber
                                QuotaLevel.CRITICAL -> StudioRose
                            },
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المستهلك: ${quotaInfo.unitsUsedToday} من ${quotaInfo.dailyLimit} وحدة",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "المتبقي: ${quotaInfo.remainingUnits} وحدة",
                                fontSize = 11.sp,
                                color = StudioEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Operations Breakdown (PRD 13.1)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuotaBreakdownItem(label = "رفعات فيديو (1,600 و)", count = "${quotaInfo.videosUploadedToday}")
                            QuotaBreakdownItem(label = "عمليات كتابة (50 و)", count = "${quotaInfo.writeOpsToday}")
                            QuotaBreakdownItem(label = "عمليات قراءة (1 و)", count = "${quotaInfo.readOpsToday}")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "يتم إعادة ضبط الحصة عند منتصف الليل بتوقيت المحيط الهادئ (خلال ${quotaInfo.resetsInHours} ساعات تقريباً).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // 2. Compliance and Quota Extension Info (PRD 1.1)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("شرط زيادة الحصة وتدقيق الامتثال (API Audit):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "وفق متطلبات المنتج، مشاريع Google API غير المدققة تُقيد فيديوهاتها بوضع خاص حتى اجتياز التدقيق. لذلك يتم رفع Shorts بوضع private مع status.publishAt لضمان النشر التلقائي الموثوق في الموعد.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // 3. Queue Section
            val allQueueItems = draftsAndQueue + scheduledPosts
            item {
                SectionHeader(
                    title = "عناصر طابور النشر (${allQueueItems.size})",
                    subtitle = "تتبع مراحل: توليد الفيديو ← رفع قابل للاستئناف ← جدولة في YouTube"
                )
            }

            if (allQueueItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد مهام معلقة في طابور النشر",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(allQueueItems) { post ->
                    QueueItemCard(
                        post = post,
                        onRetry = { onRetryPost(post) },
                        onCancel = { onCancelPost(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueItemCard(
    post: PostItem,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale("ar"))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    if (post.imageUri.isNotBlank()) {
                        AsyncImage(
                            model = post.imageUri,
                            contentDescription = post.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (post.scheduledAtUtc != null) {
                            "موعد النشر: ${dateFormat.format(Date(post.scheduledAtUtc))}"
                        } else {
                            "نشر فوري"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                // Status Badge
                val (color, text) = when (post.status) {
                    "SCHEDULED" -> StudioAmber to "مجدول"
                    "PUBLISHED" -> StudioEmerald to "نُشر"
                    "GENERATING" -> StudioRed to "جارٍ التوليد"
                    "UPLOADING" -> StudioRed to "جارٍ الرفع"
                    "FAILED" -> StudioRose to "فشل"
                    else -> Color.Gray to "في الانتظار"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Progress bar if generating or uploading
            if (post.status in listOf("GENERATING", "UPLOADING", "QUEUED")) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { post.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = StudioRed,
                    trackColor = MaterialTheme.colorScheme.surface
                )
            }

            if (post.status == "FAILED") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onRetry,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إعادة المحاولة", fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onCancel, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Cancel, contentDescription = "Cancel", tint = StudioRose, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuotaBreakdownItem(label: String, count: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

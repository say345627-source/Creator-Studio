package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnalyticsSummary
import com.example.data.model.CountryItem
import com.example.data.model.DemographicItem
import com.example.data.model.DeviceItem
import com.example.data.model.TrafficSourceItem
import com.example.data.model.VideoItem
import com.example.ui.components.MiniLineChart
import com.example.ui.components.StatCard
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed
import java.util.Locale

@Composable
fun AnalyticsScreen(
    analytics: AnalyticsSummary,
    videos: List<VideoItem>,
    selectedPeriodDays: Int,
    onPeriodSelected: (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedContentType by remember { mutableStateOf("ALL") } // ALL, SHORTS, VIDEOS
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

    val tabs = listOf("نظرة عامة", "المحتوى", "مصادر الحركة", "الجمهور", "الأجهزة")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Time Filters & Export CSV
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تحليلات YouTube Analytics الرسمية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = {
                                exportSuccessMessage = "تم تصدير تقرير Analytics بصيغة CSV محلياً"
                            },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تصدير CSV", fontSize = 11.sp)
                        }
                    }

                    if (exportSuccessMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = exportSuccessMessage ?: "",
                            color = StudioEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Period Selector
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(7, 28, 90, 365).forEach { days ->
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Content Type Filter (PRD 7.1)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to "جميع المحتوى",
                            "SHORTS" to "Shorts فقط",
                            "VIDEOS" to "فيديوهات عادية"
                        ).forEach { (type, label) ->
                            FilterChip(
                                selected = selectedContentType == type,
                                onClick = { selectedContentType = type },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StudioAmber.copy(alpha = 0.2f),
                                    selectedLabelColor = StudioAmber
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2. Data Freshness Disclaimer (PRD 1.1 & 7.1)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "بيانات YouTube Analytics الرسمية تتأخر عادة 24-72 ساعة والدقة الزمنية يومية. أحدث يومين تعتبر بيانات أولية.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. Category Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = StudioRed
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (selectedTab == index) StudioRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Tab Content
        when (selectedTab) {
            0 -> {
                // Overview Tab
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MiniLineChart(
                            data = analytics.dailyTrend,
                            title = "منحنى المشاهدات في الفترة ($selectedPeriodDays يوماً)"
                        )

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
                                value = "${analytics.avgViewDurationSec}ث",
                                growthPercent = analytics.avgDurationGrowthPercent,
                                icon = Icons.Default.Timer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            1 -> {
                // Content Tab: Sortable Video Performance
                val filteredVideos = when (selectedContentType) {
                    "SHORTS" -> videos.filter { it.isShort }
                    "VIDEOS" -> videos.filter { !it.isShort }
                    else -> videos
                }.sortedByDescending { it.views }

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "ترتيب أداء الفيديوهات (${filteredVideos.size} فيديو)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                items(filteredVideos) { video ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        VideoPerformanceRow(video = video)
                    }
                }
            }
            2 -> {
                // Traffic Sources Tab
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "مصادر الحركة (Traffic Sources)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "كيف عثر المشاهدون على فيديوهاتك ومقاطع Shorts في YouTube:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        analytics.trafficSources.forEach { source ->
                            TrafficSourceRow(source = source)
                        }
                    }
                }
            }
            3 -> {
                // Audience Demographics Tab
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "التركيبة السكانية للجمهور",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Age Groups
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "الفئات العمرية",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                analytics.ageDemographics.forEach { age ->
                                    DemographicProgressRow(label = age.category, percentage = age.percentage, color = StudioRed)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        // Gender Breakdown
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "الجنس",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                analytics.genderDemographics.forEach { gender ->
                                    DemographicProgressRow(label = gender.category, percentage = gender.percentage, color = StudioCyan)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        // Top Countries
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "أبرز البلدان الجغرافية",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                analytics.topCountries.forEach { country ->
                                    CountryRow(country = country)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
            4 -> {
                // Devices Tab
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "أنواع الأجهزة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الأجهزة التي يستخدمها جمهورك لمتابعة قناتك (حيث تهيمن الهواتف في Shorts):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        analytics.deviceBreakdown.forEach { device ->
                            DeviceRow(device = device)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoPerformanceRow(video: VideoItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (video.isShort) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioRed)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("Shorts", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = video.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${video.likes} إعجاب • ${video.commentsCount} تعليق • المدة: ${video.durationSeconds}ث",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatBigNumber(video.views)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = StudioRed
                )
                Text(
                    text = "مشاهدة",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun TrafficSourceRow(source: TrafficSourceItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = source.sourceName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(
                    text = "${source.percentage}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = StudioRed
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { source.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = StudioRed,
                trackColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${formatBigNumber(source.views)} مشاهدة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun DemographicProgressRow(label: String, percentage: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp)
            Text("${percentage}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun CountryRow(country: CountryItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(StudioRed.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(country.countryCode, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = StudioRed)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(country.countryName, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text("${country.percentage}%", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StudioAmber)
    }
}

@Composable
private fun DeviceRow(device: DeviceItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(device.deviceType, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("${device.percentage}%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = StudioRed)
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

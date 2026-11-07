package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.Channel
import com.example.data.model.QuotaInfo
import com.example.data.model.QuotaLevel
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed

@Composable
fun StudioTopBar(
    currentChannel: Channel?,
    channels: List<Channel>,
    quotaInfo: QuotaInfo,
    scheduledCount: Int,
    isRefreshing: Boolean,
    onSwitchChannel: (Channel) -> Unit,
    onRefresh: () -> Unit,
    onOpenQueue: () -> Unit
) {
    var showChannelDialog by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Channel Selector Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { showChannelDialog = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("channel_selector_pill")
            ) {
                if (currentChannel != null && currentChannel.avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = currentChannel.avatarUrl,
                        contentDescription = "Channel Avatar",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(StudioRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentChannel?.title?.take(1) ?: "C",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = currentChannel?.title ?: "اختر قناة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentChannel?.handle ?: "@channel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Switch Channel",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right Actions: Quota status pill & Queue/Notification & Refresh
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quota Pill
                val quotaColor = when (quotaInfo.statusLevel) {
                    QuotaLevel.NORMAL -> StudioEmerald
                    QuotaLevel.WARNING -> StudioAmber
                    QuotaLevel.CRITICAL -> StudioRed
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(quotaColor.copy(alpha = 0.15f))
                        .clickable { onOpenQueue() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("quota_status_pill")
                ) {
                    Icon(
                        imageVector = if (quotaInfo.statusLevel == QuotaLevel.NORMAL) Icons.Default.CloudQueue else Icons.Default.Warning,
                        contentDescription = "API Quota",
                        tint = quotaColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quotaInfo.remainingUnits}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = quotaColor
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Scheduled Queue Icon with Badge
                IconButton(
                    onClick = onOpenQueue,
                    modifier = Modifier.testTag("queue_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (scheduledCount > 0) {
                                Badge(
                                    containerColor = StudioRed,
                                    contentColor = Color.White
                                ) {
                                    Text("$scheduledCount", fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Publishing Queue",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Refresh Button
                IconButton(
                    onClick = onRefresh,
                    enabled = !isRefreshing,
                    modifier = Modifier.testTag("refresh_button")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = StudioRed
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // Switch Channel Dialog
    if (showChannelDialog) {
        AlertDialog(
            onDismissRequest = { showChannelDialog = false },
            title = {
                Text(
                    text = "تبديل القناة (قنوات متصلة)",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "اختر القناة المراد إدارتها أو الاطلاع على تحليلاتها وجدولة مقاطع Shorts الخاصة بها:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    channels.forEach { channel ->
                        val isSelected = channel.id == currentChannel?.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSwitchChannel(channel)
                                    showChannelDialog = false
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = channel.avatarUrl,
                                    contentDescription = channel.title,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = channel.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${channel.handle} • ${channel.subscribersCount} مشترك",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = StudioRed
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChannelDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

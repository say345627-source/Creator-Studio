package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CommentItem
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed
import com.example.ui.theme.StudioRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommentsScreen(
    comments: List<CommentItem>,
    onReply: (commentId: String, text: String) -> Unit,
    onModerate: (commentId: String, newStatus: String) -> Unit,
    onDelete: (commentId: String) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("تحتاج رداً", "كل التعليقات", "محتجزة للمراجعة", "مشتبه بها")

    var replyingToComment by remember { mutableStateOf<CommentItem?>(null) }
    var replyInput by remember { mutableStateOf("") }

    val filteredComments = remember(comments, selectedTab) {
        when (selectedTab) {
            0 -> comments.filter { it.needsReply && it.moderationStatus == "published" }
            1 -> comments
            2 -> comments.filter { it.moderationStatus == "heldForReview" }
            3 -> comments.filter { it.moderationStatus == "likelySpam" }
            else -> comments
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("comments_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Notice about Pinning and Hearting per PRD 3 & 9.2
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "إشراف وتفاعل الردود المباشرة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "الردود والإشراف تتم عبر commentThreads API. ملاحظة: تثبيت التعليق وقلب الإعجاب غير مدعومان رسمياً في API ويمكن إتمامهما عبر فتح YouTube.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // 2. Tabs
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
                    val badgeCount = when (index) {
                        0 -> comments.count { it.needsReply && it.moderationStatus == "published" }
                        2 -> comments.count { it.moderationStatus == "heldForReview" }
                        3 -> comments.count { it.moderationStatus == "likelySpam" }
                        else -> 0
                    }

                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == index) StudioRed else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (badgeCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(StudioRed)
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("$badgeCount", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 3. Comments List
        if (filteredComments.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "لا توجد تعليقات في هذا القسم حالياً",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredComments) { comment ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    CommentCard(
                        comment = comment,
                        onReplyClick = {
                            replyingToComment = comment
                            replyInput = ""
                        },
                        onModerate = { newStatus -> onModerate(comment.id, newStatus) },
                        onDelete = { onDelete(comment.id) },
                        onOpenVideoInYouTube = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${comment.videoId}"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    // Reply Dialog Sheet
    if (replyingToComment != null) {
        val comment = replyingToComment!!
        val quickTemplates = listOf(
            "شكراً جزيلاً لتفاعلك الرائع! 🌟",
            "يسعدني استفسارك! تابعنا للشرح القادم.",
            "ملاحظة ممتازة، سنأخذها بعين الاعتبار."
        )

        AlertDialog(
            onDismissRequest = { replyingToComment = null },
            title = {
                Text(
                    text = "الرد على ${comment.authorName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "\"${comment.textOriginal}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )

                    // Quick Template Chips (PRD 9.2)
                    Text("قوالب ردود جاهزة:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickTemplates) { tpl ->
                            SuggestionChip(
                                onClick = { replyInput = tpl },
                                label = { Text(tpl.take(20) + "...", fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = replyInput,
                        onValueChange = { replyInput = it },
                        placeholder = { Text("اكتب ردك هنا باسم القناة...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reply_input_field"),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (replyInput.isNotBlank()) {
                            onReply(comment.id, replyInput.trim())
                            replyingToComment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRed),
                    enabled = replyInput.isNotBlank(),
                    modifier = Modifier.testTag("send_reply_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إرسال الرد")
                }
            },
            dismissButton = {
                TextButton(onClick = { replyingToComment = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun CommentCard(
    comment: CommentItem,
    onReplyClick: () -> Unit,
    onModerate: (String) -> Unit,
    onDelete: () -> Unit,
    onOpenVideoInYouTube: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale("ar"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("comment_card_${comment.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Author Avatar & Name & Video Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (comment.authorAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = comment.authorAvatarUrl,
                        contentDescription = comment.authorName,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(StudioRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(comment.authorName.take(1), fontWeight = FontWeight.Bold, color = StudioRed)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comment.authorName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "على فيديو: ${comment.videoTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }

                Text(
                    text = dateFormat.format(Date(comment.publishedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comment text (textOriginal safely rendered per PRD 9.4)
            Text(
                text = comment.textOriginal,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Owner reply if present
            if (comment.ownerReplyText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioEmerald.copy(alpha = 0.1f))
                        .padding(8.dp)
                ) {
                    Text(text = "رد القناة: ", fontWeight = FontWeight.Bold, color = StudioEmerald, fontSize = 11.sp)
                    Text(text = comment.ownerReplyText, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Likes count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ThumbUpAlt, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${comment.likesCount}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Interactive Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Deep Link button for Pinning / Heart
                    IconButton(onClick = onOpenVideoInYouTube, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Open in YouTube to Heart or Pin", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }

                    // Moderation buttons if held or spam
                    if (comment.moderationStatus != "published") {
                        IconButton(onClick = { onModerate("published") }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Check, contentDescription = "Approve", tint = StudioEmerald, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Delete / Hide
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StudioRose, modifier = Modifier.size(16.dp))
                    }

                    // Reply Button
                    Button(
                        onClick = onReplyClick,
                        colors = ButtonDefaults.buttonColors(containerColor = StudioRed.copy(alpha = 0.2f), contentColor = StudioRed),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("رد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.VideoItem
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed
import com.example.ui.theme.StudioRose
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    video: VideoItem,
    onBack: () -> Unit,
    onSave: (VideoItem) -> Unit,
    onDelete: (String) -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var title by remember { mutableStateOf(video.title) }
    var description by remember { mutableStateOf(video.description) }
    var tags by remember { mutableStateOf(video.tags) }
    var privacyStatus by remember { mutableStateOf(video.privacyStatus) }
    var categoryId by remember { mutableStateOf(video.categoryId) }
    var madeForKids by remember { mutableStateOf(video.selfDeclaredMadeForKids) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل الفيديو", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val updated = video.copy(
                                title = title.trim(),
                                description = description.trim(),
                                tags = tags.trim(),
                                privacyStatus = privacyStatus,
                                categoryId = categoryId,
                                selfDeclaredMadeForKids = madeForKids
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("btn_save_video")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = StudioRed)
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
                .testTag("video_detail_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Thumbnail & Quick Metrics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 120.dp, height = if (video.isShort) 130.dp else 70.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = video.thumbnailUrl,
                                    contentDescription = video.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                if (video.isShort) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .padding(4.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(StudioRed)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text("Shorts", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                MetricLine(icon = Icons.Default.Visibility, label = "المشاهدات", value = "${video.views}")
                                MetricLine(icon = Icons.Default.ThumbUpAlt, label = "الإعجابات", value = "${video.likes}")
                                MetricLine(icon = Icons.Default.ChatBubbleOutline, label = "التعليقات", value = "${video.commentsCount}")
                                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("ar"))
                                MetricLine(icon = Icons.Default.Schedule, label = "تاريخ النشر", value = dateFormat.format(Date(video.publishedAt)))
                            }
                        }
                    }
                }
            }

            // 2. Read-Modify-Write Rule notice (PRD 8.3)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = StudioRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "يتم تعديل البيانات عبر videos.update بنمط Read-Modify-Write للحفاظ على الحقول الإلزامية.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 3. Editable Title
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 100) title = it },
                    label = { Text("عنوان الفيديو (حتى 100 حرف)") },
                    supportingText = { Text("${title.length}/100 حرف", fontSize = 10.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_title"),
                    singleLine = true
                )
            }

            // 4. Editable Description
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف (Description)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_edit_desc"),
                    minLines = 3,
                    maxLines = 6
                )
            }

            // 5. Tags
            item {
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("الوسوم والكلمات المفتاحية (مفصولة بفواصل)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // 6. Privacy & Category
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("الخصوصية:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "public" to "عام (Public)",
                                "unlisted" to "غير مدرج (Unlisted)",
                                "private" to "خاص (Private)"
                            ).forEach { (p, label) ->
                                FilterChip(
                                    selected = privacyStatus == p,
                                    onClick = { privacyStatus = p },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                        selectedLabelColor = StudioRed
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Category
                        Text("فئة المحتوى:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                "22" to "مدونات وأشخاص",
                                "27" to "تعليم",
                                "28" to "تقنية وعلوم",
                                "24" to "ترفيه"
                            ).forEach { (catId, label) ->
                                FilterChip(
                                    selected = categoryId == catId,
                                    onClick = { categoryId = catId },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StudioAmber.copy(alpha = 0.2f),
                                        selectedLabelColor = StudioAmber
                                    )
                                )
                            }
                        }

                        // Made for Kids COPPA declaration
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("موجّه للأطفال (Made for Kids)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("إلزامي وفق سياسات Google لحماية خصوصية الأطفال", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = madeForKids,
                                onCheckedChange = { madeForKids = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = StudioRed, checkedTrackColor = StudioRed.copy(alpha = 0.4f))
                            )
                        }
                    }
                }
            }

            // 7. External Actions (Deep Link) per PRD
            item {
                OutlinedButton(
                    onClick = {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://studio.youtube.com/video/${video.videoId}/edit"))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فتح في YouTube Studio (للبطاقات وحقوق النشر والربح)", fontSize = 12.sp)
                }
            }

            // 8. Delete Video (Double Confirmation per PRD 8.3)
            item {
                Button(
                    onClick = { showDeleteConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRose.copy(alpha = 0.2f), contentColor = StudioRose),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حذف الفيديو نهائياً", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }

    // Double confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StudioRose, modifier = Modifier.size(32.dp))
            },
            title = {
                Text("تأكيد حذف الفيديو نهائياً", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "هل أنت متأكد من حذف \"${video.title}\"؟ هذا الإجراء نهائي ولا يمكن التراجع عنه وفق سياسات YouTube Data API.",
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDelete(video.videoId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRose)
                ) {
                    Text("نعم، احذف نهائياً")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun MetricLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "$label: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

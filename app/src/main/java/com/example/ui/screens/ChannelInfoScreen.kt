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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.data.model.Channel
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelInfoScreen(
    channel: Channel?,
    onBack: () -> Unit,
    onSave: (title: String, description: String, keywords: String, country: String) -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var description by remember(channel) { mutableStateOf(channel?.description ?: "") }
    var keywords by remember(channel) { mutableStateOf(channel?.keywords ?: "") }
    var country by remember(channel) { mutableStateOf(channel?.country ?: "SA") }
    var savedFeedback by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("معلومات القناة (Channel Info)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (channel != null) {
                                onSave(channel.title, description, keywords, country)
                                savedFeedback = true
                            }
                        },
                        modifier = Modifier.testTag("btn_save_channel")
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
                .testTag("channel_info_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Channel Identity & Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // Banner Image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .background(StudioRed.copy(alpha = 0.3f))
                        ) {
                            if (channel?.bannerUrl?.isNotBlank() == true) {
                                AsyncImage(
                                    model = channel.bannerUrl,
                                    contentDescription = "Banner",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // Avatar & Title
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (channel?.avatarUrl?.isNotBlank() == true) {
                                AsyncImage(
                                    model = channel.avatarUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(StudioRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(channel?.title?.take(1) ?: "C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(channel?.title ?: "القناة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(channel?.handle ?: "@handle", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${channel?.subscribersCount ?: 0} مشترك • ${channel?.videosCount ?: 0} فيديو", fontSize = 11.sp, color = StudioAmber)
                            }
                        }
                    }
                }
            }

            // 2. Policy Notice on Channel Name/Avatar Change per PRD 3 & 10.1
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = StudioAmber, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تغيير اسم القناة أو صورتها:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "وفق قيود Google الرسمية، لا تسمح واجهة API بتغيير اسم القناة أو صورتها الشخصية برمجياً. يتم التعديل عبر إعدادات حساب Google أو تطبيق YouTube.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.OpenInNew, contentDescription = "Open Account", tint = StudioRed)
                        }
                    }
                }
            }

            if (savedFeedback) {
                item {
                    Text(
                        text = "تم حفظ تحديثات القناة بنجاح عبر channels.update",
                        color = Color(0xFF10B981),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3. Editable Fields (channels.update)
            item {
                Text("البيانات القابلة للتعديل عبر API:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { if (it.length <= 1000) description = it },
                    label = { Text("وصف القناة (حتى 1000 حرف)") },
                    supportingText = { Text("${description.length}/1000 حرف", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            }

            item {
                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("الكلمات المفتاحية للقناة (Keywords)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("رمز البلد (Country Code - e.g. SA, EG, AE)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Button(
                    onClick = {
                        if (channel != null) {
                            onSave(channel.title, description, keywords, country)
                            savedFeedback = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ التغييرات", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

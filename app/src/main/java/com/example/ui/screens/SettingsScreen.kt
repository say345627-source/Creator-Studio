package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VpnKey
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed
import com.example.ui.theme.StudioRose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentChannel: Channel?,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var isArabic by remember { mutableStateOf(true) }
    var autoAddShortsTag by remember { mutableStateOf(true) }
    var uploadOnWifiOnly by remember { mutableStateOf(false) }
    var showDisconnectDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات والحساب", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
                .testTag("settings_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Google Account & Active Channel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = StudioRed, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("الحساب المتصل عبر Google Identity", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "القناة النشطة: ${currentChannel?.title ?: "غير محدد"} (${currentChannel?.handle ?: ""})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "معرّف القناة: ${currentChannel?.id ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // 2. OAuth Scopes & Granular Consent (PRD 5.2)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = StudioEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("صلاحيات OAuth 2.0 الممنوحة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        ScopeItemRow(scope = "youtube.readonly", desc = "قراءة القناة والفيديوهات والتعليقات", granted = true)
                        ScopeItemRow(scope = "yt-analytics.readonly", desc = "قراءة تقارير YouTube Analytics الرسمية", granted = true)
                        ScopeItemRow(scope = "youtube.force-ssl", desc = "كتابة: رفع وجدولة Shorts والرد والإشراف", granted = true)

                        Text(
                            text = "يتبع التطبيق مبدأ أقل صلاحية (Principle of Least Privilege).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // 3. Publishing Defaults
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
                        Text("إعدادات النشر الافتراضية", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("إضافة وسم #Shorts تلقائياً للوصف", fontSize = 12.sp)
                            Switch(
                                checked = autoAddShortsTag,
                                onCheckedChange = { autoAddShortsTag = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = StudioRed, checkedTrackColor = StudioRed.copy(alpha = 0.4f))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("الرفع والجدولة عبر Wi-Fi فقط", fontSize = 12.sp)
                            Switch(
                                checked = uploadOnWifiOnly,
                                onCheckedChange = { uploadOnWifiOnly = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = StudioRed, checkedTrackColor = StudioRed.copy(alpha = 0.4f))
                            )
                        }
                    }
                }
            }

            // 4. Language & Theme
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("اللغة والمظهر", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = isArabic,
                                onClick = { isArabic = true },
                                label = { Text("العربية (RTL)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                    selectedLabelColor = StudioRed
                                )
                            )
                            FilterChip(
                                selected = !isArabic,
                                onClick = { isArabic = false },
                                label = { Text("English (LTR)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                    selectedLabelColor = StudioRed
                                )
                            )
                        }
                    }
                }
            }

            if (feedbackMessage != null) {
                item {
                    Text(
                        text = feedbackMessage ?: "",
                        color = StudioEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. Cache & Privacy & Disconnect (PRD 5.4)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("الخصوصية وإدارة البيانات", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                        OutlinedButton(
                            onClick = {
                                feedbackMessage = "تم مسح الذاكرة المؤقتة المحلية بنجاح"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("مسح الذاكرة المؤقتة (Clear Cache)")
                        }

                        Button(
                            onClick = { showDisconnectDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = StudioRose.copy(alpha = 0.2f), contentColor = StudioRose),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فصل الحساب وحذف البيانات المحلية", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 6. External Legal Links
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/t/terms"))
                        context.startActivity(intent)
                    }) {
                        Text("شروط خدمة YouTube", fontSize = 11.sp)
                    }

                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/privacy"))
                        context.startActivity(intent)
                    }) {
                        Text("سياسة خصوصية Google", fontSize = 11.sp)
                    }
                }
            }

            item {
                Text(
                    text = "تطبيق Creator Studio غير تابع رسمي لشركة YouTube أو Google. جميع العلامات التجارية ملك لأصحابها.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }
        }
    }

    if (showDisconnectDialog) {
        AlertDialog(
            onDismissRequest = { showDisconnectDialog = false },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = StudioRose, modifier = Modifier.size(32.dp))
            },
            title = {
                Text("تأكيد فصل الحساب وحذف البيانات", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "سيؤدي هذا الإجراء إلى حذف قاعدة البيانات المؤقتة المحلية وإلغاء الجلسة.\n\nتأكيد هام: الفيديوهات المرفوعة والمجدولة داخل YouTube لن تتأثر وستبقى محفوظة ومجدولة في حسابك.",
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDisconnectDialog = false
                        feedbackMessage = "تم فصل الحساب بنجاح وإلغاء الرموز المحلية"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRose)
                ) {
                    Text("تأكيد الفصل")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisconnectDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun ScopeItemRow(scope: String, desc: String, granted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (granted) Icons.Default.Check else Icons.Default.Lock,
            contentDescription = null,
            tint = if (granted) StudioEmerald else StudioRose,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(scope, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed

@Composable
fun ConnectAccountDialog(
    userEmail: String = "abdallhtaktok@gmail.com",
    projectId: String = "mednotes-cf48d",
    onDismiss: () -> Unit,
    onConnectChannel: (title: String, handle: String, customId: String?, subscribersCount: Long) -> Unit
) {
    var channelTitle by remember { mutableStateOf("Abdallah Taktok") }
    var channelHandle by remember { mutableStateOf("@AbdallahTaktok") }
    var channelIdInput by remember { mutableStateOf("") }
    var subscribersInput by remember { mutableStateOf("1500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "ربط حساب وقناة YouTube",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google Account Authorized Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = StudioEmerald.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StudioEmerald, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "حساب Google OAuth موثق ومتصل",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = StudioEmerald
                            )
                            Text(
                                text = "البريد: $userEmail",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "المشروع: $projectId • النطاقات: youtube.readonly / force-ssl",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "بيانات القناة المراد ربطها وإدارتها في Creator Studio:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Quick preset chip
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("اقتراح سريع:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    SuggestionChip(
                        onClick = {
                            channelTitle = "Abdallah Taktok"
                            channelHandle = "@AbdallahTaktok"
                            subscribersInput = "2400"
                        },
                        label = { Text("@AbdallahTaktok", fontSize = 10.sp) }
                    )
                }

                OutlinedTextField(
                    value = channelTitle,
                    onValueChange = { channelTitle = it },
                    label = { Text("اسم القناة (Channel Name)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_channel_title"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = channelHandle,
                    onValueChange = { channelHandle = it },
                    label = { Text("معرّف القناة (Handle مثل @name)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_channel_handle"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = subscribersInput,
                    onValueChange = { subscribersInput = it },
                    label = { Text("عدد المشتركين التقريبي") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = channelIdInput,
                    onValueChange = { channelIdInput = it },
                    label = { Text("معرّف القناة التقني (اختياري - Channel ID)") },
                    placeholder = { Text("UCxxxxxxxxxxxxxxxxxx") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val subs = subscribersInput.toLongOrNull() ?: 100L
                    onConnectChannel(channelTitle.trim(), channelHandle.trim(), channelIdInput.trim(), subs)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = StudioRed),
                enabled = channelTitle.isNotBlank() && channelHandle.isNotBlank(),
                modifier = Modifier.testTag("btn_confirm_connect_channel")
            ) {
                Text("ربط وتفعيل القناة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

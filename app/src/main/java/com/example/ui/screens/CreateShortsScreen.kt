package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioEmerald
import com.example.ui.theme.StudioRed
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CreateShortsScreen(
    onBatchSubmitted: (
        images: List<String>,
        defaultDuration: Int,
        fitMode: String,
        motionEffect: String,
        titleTemplate: String,
        description: String,
        targetPrivacy: String,
        madeForKids: Boolean,
        isScheduled: Boolean,
        scheduleOffsetsHours: List<Int>
    ) -> Unit,
    onNavigateToQueue: () -> Unit
) {
    // Curated high quality creator image presets
    val presetImages = remember {
        listOf(
            "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1542744094-3a31f272c490?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1534972195531-a756b1126f24?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80"
        )
    }

    val selectedImages = remember { mutableStateListOf<String>() }
    var activePreviewIndex by remember { mutableIntStateOf(0) }

    // Initialize with first 3 images if empty
    LaunchedEffect(Unit) {
        if (selectedImages.isEmpty()) {
            selectedImages.addAll(presetImages.take(3))
        }
    }

    // Video settings
    var durationSec by remember { mutableFloatStateOf(3f) } // Default 3s per PRD
    var fitMode by remember { mutableStateOf("BLURRED_BG") } // BLURRED_BG, CENTER_CROP, SOLID_BG
    var motionEffect by remember { mutableStateOf("ZOOM_IN") } // ZOOM_IN, ZOOM_OUT, PAN, NONE
    var titleTemplate by remember { mutableStateOf("نصيحة اليوم #{الرقم} 🚀 #Shorts") }
    var description by remember { mutableStateOf("شاركنا رأيك في التعليقات واشترك للمزيد!\n#Shorts #CreatorStudio") }
    var targetPrivacy by remember { mutableStateOf("public") }
    var madeForKids by remember { mutableStateOf(false) } // Mandatory COPPA

    // Scheduling mode
    var isScheduled by remember { mutableStateOf(true) }
    var scheduleIntervalHours by remember { mutableIntStateOf(4) } // Every 4 hours

    // Preview player state
    var isPlaying by remember { mutableStateOf(true) }
    var previewPlaybackTime by remember { mutableFloatStateOf(0f) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Playback loop simulation
    LaunchedEffect(isPlaying, durationSec) {
        while (isPlaying) {
            delay(100)
            previewPlaybackTime = (previewPlaybackTime + 0.1f)
            if (previewPlaybackTime >= durationSec) {
                previewPlaybackTime = 0f
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("create_shorts_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // 1. Feature Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تحويل الصور إلى Shorts مجدولة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ميزة Creator Studio المميزة لجدولة ونشر دفعات الصور كفيديوهات 9:16",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. Select Photos (Multi-Selection Gallery)
        item {
            SectionHeader(
                title = "الصور المختارة (${selectedImages.size}/50)",
                subtitle = "حدد الصور من المعرض لتحويل كل صورة إلى Short مستقل"
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                itemsIndexed(presetImages) { index, imageUrl ->
                    val isSelected = selectedImages.contains(imageUrl)
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) StudioRed else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (isSelected) {
                                    if (selectedImages.size > 1) selectedImages.remove(imageUrl)
                                } else {
                                    selectedImages.add(imageUrl)
                                }
                            }
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Photo $index",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(StudioRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Live 9:16 Vertical Video Simulator
        item {
            SectionHeader(
                title = "معاينة الفيديو الناتج (9:16 Vertical)",
                subtitle = "محاكاة تأثير الحركة والأبعاد والمدة على YouTube Shorts"
            )

            val currentImg = selectedImages.getOrNull(activePreviewIndex) ?: presetImages.first()
            val infiniteTransition = rememberInfiniteTransition(label = "motion")
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = if (motionEffect == "ZOOM_IN") 1.0f else 1.15f,
                targetValue = if (motionEffect == "ZOOM_IN") 1.18f else 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween((durationSec * 1000).toInt(), easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Vertical 9:16 Canvas Box
                    Box(
                        modifier = Modifier
                            .width(170.dp)
                            .aspectRatio(9f / 16f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E2028))
                    ) {
                        // Blurred Background if selected
                        if (fitMode == "BLURRED_BG") {
                            AsyncImage(
                                model = currentImg,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(2.0f),
                                contentScale = ContentScale.Crop,
                                alpha = 0.45f
                            )
                        }

                        // Main Image with Motion Effect
                        AsyncImage(
                            model = currentImg,
                            contentDescription = "Shorts Video Frame",
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(if (motionEffect != "NONE") animatedScale else 1.0f),
                            contentScale = if (fitMode == "CENTER_CROP") ContentScale.Crop else ContentScale.Fit
                        )

                        // Shorts Brand Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(StudioRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Shorts", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Play/Pause Overlay Toggle
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        // Bottom Title & Duration Overlay
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.65f))
                                .padding(6.dp)
                        ) {
                            Text(
                                text = titleTemplate.replace("{الرقم}", (activePreviewIndex + 1).toString()).replace("{التاريخ}", "اليوم"),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.1fs / %.0fs", previewPlaybackTime, durationSec),
                                    color = StudioAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "H.264 • 1080p",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Selector for which photo to preview
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        selectedImages.forEachIndexed { idx, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (activePreviewIndex == idx) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (activePreviewIndex == idx) StudioRed else Color.Gray)
                                    .clickable { activePreviewIndex = idx }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. Video & Motion Settings
        item {
            SectionHeader(
                title = "إعدادات الفيديو والتأثيرات",
                subtitle = "المدة، طريقة الملاءمة وحركة الكاميرا"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Duration Slider (1s - 60s, default 3s per PRD)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مدة الفيديو:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            "${durationSec.toInt()} ثوانٍ (الافتراضي 3ث)",
                            color = StudioRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Slider(
                        value = durationSec,
                        onValueChange = { durationSec = it },
                        valueRange = 1f..60f,
                        steps = 58,
                        colors = SliderDefaults.colors(thumbColor = StudioRed, activeTrackColor = StudioRed),
                        modifier = Modifier.testTag("duration_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fitting Mode
                    Text("طريقة ملاءمة الصورة في إطار 9:16:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "BLURRED_BG" to "خلفية مموهة",
                            "CENTER_CROP" to "اقتصاص مركزي",
                            "SOLID_BG" to "لون ثابت"
                        ).forEach { (mode, label) ->
                            FilterChip(
                                selected = fitMode == mode,
                                onClick = { fitMode = mode },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                    selectedLabelColor = StudioRed
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Motion Effect
                    Text("حركة الصورة (Ken Burns):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "ZOOM_IN" to "تكبير بطيء",
                            "ZOOM_OUT" to "تصغير بطيء",
                            "PAN" to "تحريك أفقي",
                            "NONE" to "بدون حركة"
                        ).forEach { (effect, label) ->
                            FilterChip(
                                selected = motionEffect == effect,
                                onClick = { motionEffect = effect },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                    selectedLabelColor = StudioRed
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 5. Metadata & Policy Declarations
        item {
            SectionHeader(
                title = "العنوان والوصف والسياسات",
                subtitle = "قوالب العناوين التلقائية وإفصاح محتوى الأطفال الإلزامي"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = titleTemplate,
                        onValueChange = { titleTemplate = it },
                        label = { Text("قالب العنوان (يدعم {الرقم} و {التاريخ})") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_title_template"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("الوصف العام لجميع مقاطع الدفعة") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    // Privacy selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الخصوصية المستهدفة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(
                                "public" to "عام",
                                "unlisted" to "غير مدرج",
                                "private" to "خاص"
                            ).forEach { (p, label) ->
                                FilterChip(
                                    selected = targetPrivacy == p,
                                    onClick = { targetPrivacy = p },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                        selectedLabelColor = StudioRed
                                    )
                                )
                            }
                        }
                    }

                    // Made for kids (Mandatory COPPA per PRD)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "محتوى موجّه للأطفال (Made for Kids)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "إقرار إلزامي وفق سياسات YouTube وحماية الأطفال COPPA",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = madeForKids,
                            onCheckedChange = { madeForKids = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = StudioRed, checkedTrackColor = StudioRed.copy(alpha = 0.4f))
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 6. Scheduling vs Instant Mode
        item {
            SectionHeader(
                title = "خطة النشر والجدولة",
                subtitle = "نشر فوري لجميع المقاطع أو جدولة كل صورة بموعد مستقل"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = isScheduled,
                            onClick = { isScheduled = true },
                            label = { Text("جدولة ذكية مستقلة", fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioAmber.copy(alpha = 0.25f),
                                selectedLabelColor = StudioAmber
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = !isScheduled,
                            onClick = { isScheduled = false },
                            label = { Text("نشر فوري الآن", fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioEmerald.copy(alpha = 0.25f),
                                selectedLabelColor = StudioEmerald
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (isScheduled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "توزيع مواعيد النشر تلقائياً:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                2 to "كل ساعتين",
                                4 to "كل 4 ساعات",
                                12 to "منشوران يومياً",
                                24 to "منشور يومياً"
                            ).forEach { (hours, label) ->
                                FilterChip(
                                    selected = scheduleIntervalHours == hours,
                                    onClick = { scheduleIntervalHours = hours },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StudioAmber.copy(alpha = 0.2f),
                                        selectedLabelColor = StudioAmber
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Timetable preview
                        val cal = Calendar.getInstance()
                        val dateFormat = SimpleDateFormat("EEEE dd MMM, hh:mm a", Locale("ar"))
                        Text(
                            text = "جدول المواعيد المتوقعة للدفعة (${selectedImages.size} مقاطع):",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        selectedImages.take(3).forEachIndexed { idx, _ ->
                            cal.add(Calendar.HOUR_OF_DAY, scheduleIntervalHours)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("صورة #${idx + 1}:", fontSize = 11.sp, color = StudioAmber)
                                Text(dateFormat.format(cal.time), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        if (selectedImages.size > 3) {
                            Text(
                                text = "... و${selectedImages.size - 3} منشورات إضافية متتالية",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 7. Quota Estimation & API Audit Transparency Card (PRD 1.1)
        item {
            val estimatedQuota = selectedImages.size * 1600
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = StudioRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "تقدير استهلاك الحصة (Quota Governor):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = StudioRed
                        )
                        Text(
                            text = "تكلف هذه العملية نحو $estimatedQuota وحدة (1,600 وحدة لكل رفع فيديو). يتم الرفع بوضع private مع الحقل status.publishAt لتتولى YouTube نشره تلقائياً.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 8. Submit Button
        item {
            Button(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_batch_button"),
                colors = ButtonDefaults.buttonColors(containerColor = StudioRed),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScheduled) "بدء التوليد والجدولة (${selectedImages.size} مقاطع)" else "بدء التوليد والنشر الفوري (${selectedImages.size} مقاطع)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Confirmation Dialog before queueing
    if (showConfirmationDialog) {
        val estimatedQuota = selectedImages.size * 1600
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StudioRed, modifier = Modifier.size(36.dp))
            },
            title = {
                Text(
                    text = "تأكيد دفعة Shorts (${selectedImages.size} صور)",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "سيقوم التطبيق بتحويل كل صورة إلى فيديو عمودي MP4 H.264 عالي الدقة (1080×1920) مع صوت صامت متوافق وحركة ${if (motionEffect == "ZOOM_IN") "تكبير" else "كاميرا"}.",
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• المدة: ${durationSec.toInt()} ثوانٍ لكل مقطع\n• الحصة المقدرة: $estimatedQuota وحدة\n• النمط: ${if (isScheduled) "جدولة مستقلة في YouTube" else "نشر فوري"}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = StudioAmber
                    )
                    Text(
                        text = "ستعمل المهمة في الخلفية في طابور النشر مع متابعة التقدم لحظياً.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        val offsets = selectedImages.indices.map { (it + 1) * scheduleIntervalHours }
                        onBatchSubmitted(
                            selectedImages.toList(),
                            durationSec.toInt(),
                            fitMode,
                            motionEffect,
                            titleTemplate,
                            description,
                            targetPrivacy,
                            madeForKids,
                            isScheduled,
                            offsets
                        )
                        onNavigateToQueue()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StudioRed)
                ) {
                    Text("ابدأ المعالجة الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PostItem
import com.example.data.model.VideoItem
import com.example.ui.components.VideoCard
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioRed

@Composable
fun ContentScreen(
    videos: List<VideoItem>,
    scheduledPosts: List<PostItem>,
    draftsAndQueue: List<PostItem>,
    onVideoClick: (VideoItem) -> Unit,
    onCreateShortClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var sortByViews by remember { mutableStateOf(false) }

    val tabs = listOf("الكل", "الفيديوهات", "Shorts", "المجدولة", "المسودات والطابور")

    val filteredVideos = remember(videos, selectedTab, searchQuery, sortByViews, scheduledPosts) {
        val baseList = when (selectedTab) {
            0 -> videos
            1 -> videos.filter { !it.isShort }
            2 -> videos.filter { it.isShort }
            3 -> videos.filter { it.publishAt != null && it.publishAt > System.currentTimeMillis() }
            else -> videos
        }

        val searchFiltered = if (searchQuery.isNotBlank()) {
            baseList.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true) ||
                        it.tags.contains(searchQuery, ignoreCase = true)
            }
        } else {
            baseList
        }

        if (sortByViews) {
            searchFiltered.sortedByDescending { it.views }
        } else {
            searchFiltered.sortedByDescending { it.publishedAt }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("content_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("content_search_field"),
                        placeholder = { Text("ابحث في عناوين الفيديوهات والوسوم...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
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
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 3. Sorting Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = !sortByViews,
                            onClick = { sortByViews = false },
                            label = { Text("الأحدث نشراً", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                selectedLabelColor = StudioRed
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = sortByViews,
                            onClick = { sortByViews = true },
                            label = { Text("الأكثر مشاهدة", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StudioRed.copy(alpha = 0.2f),
                                selectedLabelColor = StudioRed
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 4. Video List or Empty State
            if (filteredVideos.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد فيديوهات مطابقة للبحث أو الفلتر",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredVideos) { video ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)) {
                        VideoCard(video = video, onClick = { onVideoClick(video) })
                    }
                }
            }
        }

        // Floating Action Button to create Short
        FloatingActionButton(
            onClick = onCreateShortClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp)
                .testTag("fab_create_short"),
            containerColor = StudioRed,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Short", modifier = Modifier.size(26.dp))
        }
    }
}

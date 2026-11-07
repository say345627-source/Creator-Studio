package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.StudioRed

@Composable
fun StudioBottomBar(
    currentRoute: String,
    pendingCommentsCount: Int,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("studio_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // 1. Dashboard
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { onNavigate(Screen.Dashboard.route) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Dashboard.route) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = Screen.Dashboard.titleAr
                )
            },
            label = { Text(Screen.Dashboard.titleAr, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioRed,
                indicatorColor = StudioRed.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_dashboard")
        )

        // 2. Content
        NavigationBarItem(
            selected = currentRoute == Screen.Content.route,
            onClick = { onNavigate(Screen.Content.route) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Content.route) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                    contentDescription = Screen.Content.titleAr
                )
            },
            label = { Text(Screen.Content.titleAr, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioRed,
                indicatorColor = StudioRed.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_content")
        )

        // 3. Create Shorts (Prominent Feature)
        NavigationBarItem(
            selected = currentRoute == Screen.CreateShorts.route,
            onClick = { onNavigate(Screen.CreateShorts.route) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = Screen.CreateShorts.titleAr,
                    tint = StudioRed,
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text(Screen.CreateShorts.titleAr, fontSize = 11.sp, color = StudioRed) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioRed,
                indicatorColor = StudioRed.copy(alpha = 0.2f)
            ),
            modifier = Modifier.testTag("nav_create_shorts")
        )

        // 4. Comments
        NavigationBarItem(
            selected = currentRoute == Screen.Comments.route,
            onClick = { onNavigate(Screen.Comments.route) },
            icon = {
                BadgedBox(
                    badge = {
                        if (pendingCommentsCount > 0) {
                            Badge(
                                containerColor = StudioRed,
                                contentColor = Color.White
                            ) {
                                Text("$pendingCommentsCount", fontSize = 10.sp)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == Screen.Comments.route) Icons.Filled.Comment else Icons.Outlined.Comment,
                        contentDescription = Screen.Comments.titleAr
                    )
                }
            },
            label = { Text(Screen.Comments.titleAr, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioRed,
                indicatorColor = StudioRed.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_comments")
        )

        // 5. Analytics
        NavigationBarItem(
            selected = currentRoute == Screen.Analytics.route,
            onClick = { onNavigate(Screen.Analytics.route) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Screen.Analytics.route) Icons.Filled.Analytics else Icons.Outlined.Analytics,
                    contentDescription = Screen.Analytics.titleAr
                )
            },
            label = { Text(Screen.Analytics.titleAr, fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StudioRed,
                indicatorColor = StudioRed.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_analytics")
        )
    }
}

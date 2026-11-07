package com.example.ui.navigation

sealed class Screen(val route: String, val titleAr: String, val titleEn: String) {
    object Dashboard : Screen("dashboard", "اللوحة", "Dashboard")
    object Content : Screen("content", "المحتوى", "Content")
    object CreateShorts : Screen("create_shorts", "إنشاء Short", "Create Short")
    object Comments : Screen("comments", "التعليقات", "Comments")
    object Analytics : Screen("analytics", "التحليلات", "Analytics")
    
    // Sub-screens
    object PublishingQueue : Screen("publishing_queue", "طابور النشر والحصة", "Publishing Queue")
    object Playlists : Screen("playlists", "قوائم التشغيل", "Playlists")
    object ChannelInfo : Screen("channel_info", "معلومات القناة", "Channel Info")
    object Settings : Screen("settings", "الإعدادات", "Settings")
    object VideoDetail : Screen("video_detail", "تفاصيل الفيديو", "Video Detail")
}

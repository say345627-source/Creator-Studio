package com.example.data.model

data class QuotaInfo(
    val dailyLimit: Int = 10000,
    val unitsUsedToday: Int = 3450,
    val videosUploadedToday: Int = 2,
    val writeOpsToday: Int = 5,
    val readOpsToday: Int = 38,
    val resetsInHours: Int = 9,
    val statusLevel: QuotaLevel = QuotaLevel.NORMAL
) {
    val remainingUnits: Int get() = (dailyLimit - unitsUsedToday).coerceAtLeast(0)
    val percentageUsed: Float get() = (unitsUsedToday.toFloat() / dailyLimit.toFloat()).coerceIn(0f, 1f)
}

enum class QuotaLevel {
    NORMAL,
    WARNING, // > 75%
    CRITICAL // > 90%
}

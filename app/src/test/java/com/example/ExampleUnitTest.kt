package com.example

import com.example.data.model.QuotaInfo
import com.example.data.model.QuotaLevel
import com.example.data.repository.CreatorRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testQuotaCalculation() {
        val quota = QuotaInfo(
            dailyLimit = 10000,
            unitsUsedToday = 3450,
            videosUploadedToday = 2,
            writeOpsToday = 5,
            readOpsToday = 42,
            resetsInHours = 9,
            statusLevel = QuotaLevel.NORMAL
        )

        assertEquals(6550, quota.remainingUnits)
        assertTrue(quota.percentageUsed in 0.34f..0.35f)
        assertEquals(QuotaLevel.NORMAL, quota.statusLevel)
    }

    @Test
    fun testAnalyticsGeneration() {
        val analytics = CreatorRepository.createInitialAnalytics(28)
        assertEquals(28, analytics.periodDays)
        assertTrue(analytics.totalViews > 0)
        assertTrue(analytics.dailyTrend.isNotEmpty())
        assertEquals(28, analytics.dailyTrend.size)
        assertTrue(analytics.trafficSources.isNotEmpty())
    }
}

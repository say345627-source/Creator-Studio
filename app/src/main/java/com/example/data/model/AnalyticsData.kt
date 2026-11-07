package com.example.data.model

data class DailyMetric(
    val dateLabel: String,
    val timestamp: Long,
    val views: Long,
    val watchMinutes: Long,
    val subscribersGained: Int
)

data class TrafficSourceItem(
    val sourceName: String,
    val percentage: Float,
    val views: Long
)

data class DemographicItem(
    val category: String,
    val percentage: Float
)

data class CountryItem(
    val countryCode: String,
    val countryName: String,
    val percentage: Float,
    val views: Long
)

data class DeviceItem(
    val deviceType: String,
    val percentage: Float
)

data class AnalyticsSummary(
    val periodDays: Int = 28,
    val totalViews: Long = 184520,
    val viewsGrowthPercent: Double = 18.4,
    val totalWatchHours: Double = 1240.5,
    val watchHoursGrowthPercent: Double = 12.1,
    val netSubscribers: Int = 1420,
    val subscribersGrowthPercent: Double = 24.8,
    val avgViewDurationSec: Int = 42,
    val avgDurationGrowthPercent: Double = 5.2,
    val dailyTrend: List<DailyMetric> = emptyList(),
    val trafficSources: List<TrafficSourceItem> = emptyList(),
    val ageDemographics: List<DemographicItem> = emptyList(),
    val genderDemographics: List<DemographicItem> = emptyList(),
    val topCountries: List<CountryItem> = emptyList(),
    val deviceBreakdown: List<DeviceItem> = emptyList()
)

package com.example

import com.example.data.model.PostItem
import com.example.data.model.VideoItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

    @Test
    fun testPostItemDefaultsAndShortsValidation() {
        val post = PostItem(
            batchId = "batch_001",
            channelId = "UC_TEST",
            imageUri = "https://example.com/test.jpg",
            title = "نصيحة إبداعية #Shorts",
            durationSeconds = 3,
            fittingMode = "BLURRED_BG",
            motionEffect = "ZOOM_IN"
        )

        assertEquals(3, post.durationSeconds)
        assertEquals("BLURRED_BG", post.fittingMode)
        assertEquals("ZOOM_IN", post.motionEffect)
        assertTrue(post.title.contains("#Shorts"))
    }

    @Test
    fun testVideoItemScheduleCalculation() {
        val futureTime = System.currentTimeMillis() + 86400000L
        val video = VideoItem(
            videoId = "v_123",
            channelId = "UC_TEST",
            title = "فيديو مجدول",
            privacyStatus = "private",
            publishAt = futureTime,
            isShort = true
        )

        assertTrue(video.isShort)
        assertEquals("private", video.privacyStatus)
        assertNotNull(video.publishAt)
        assertTrue(video.publishAt!! > System.currentTimeMillis())
    }
}

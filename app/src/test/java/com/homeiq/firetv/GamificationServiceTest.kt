package com.homeiq.firetv

import com.homeiq.firetv.services.GamificationService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GamificationServiceTest {

    private lateinit var gamificationService: GamificationService

    @Before
    fun setUp() {
        gamificationService = GamificationService()
    }

    @Test
    fun `eco streak increments on daily savings`() {
        val streak = gamificationService.getDefaultStreak()
        assertEquals(7, streak.days)
        assertTrue(streak.weeklySavingsRupees > 0)
        assertEquals(10, streak.nextMilestoneDays)
    }
}

package com.homeiq.firetv

import com.homeiq.firetv.services.BedrockHomeService
import com.homeiq.firetv.services.HomeDataService
import org.junit.Assert.*
import org.junit.Test

class BedrockFallbackTest {

    @Test
    fun `bedrock fallback returns deterministic response offline`() {
        val state = HomeDataService.getDefaultHome()
        val insight = BedrockHomeService.getEnergyInsight(state)
        assertNotNull(insight)
        assertTrue(insight.isNotEmpty())

        val securityInsight = BedrockHomeService.getSecurityInsight(state)
        assertNotNull(securityInsight)
        assertTrue(securityInsight.isNotEmpty())

        val morningGreeting = BedrockHomeService.generateProactiveGreeting(state, 8)
        assertTrue(morningGreeting.contains("Good morning"))
    }
}

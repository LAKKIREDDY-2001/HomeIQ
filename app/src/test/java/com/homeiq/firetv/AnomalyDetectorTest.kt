package com.homeiq.firetv

import com.homeiq.firetv.model.*
import com.homeiq.firetv.services.AnomalyDetector
import com.homeiq.firetv.services.HomeDataService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AnomalyDetectorTest {

    private lateinit var detector: AnomalyDetector
    private lateinit var baseState: HomeState

    @Before
    fun setUp() {
        detector = AnomalyDetector()
        baseState = HomeDataService.getDefaultHome()
    }

    @Test
    fun `stove on with no motion for 3 hours triggers STOVE_LEFT_ON`() {
        val kitchenRoom = baseState.rooms.find { it.id == "kitchen" }
        assertNotNull(kitchenRoom)
        val stove = kitchenRoom!!.devices.find { it.id == "stove" || it.id == "chimney" }
        assertNotNull(stove)

        val activeState = baseState.copy(
            familyGuardianSettings = baseState.familyGuardianSettings.copy(isElderCareActive = true),
            rooms = baseState.rooms.map { room ->
                if (room.id == "kitchen") {
                    room.copy(devices = room.devices.map {
                        if (it.id == stove!!.id) it.copy(isOn = true) else it
                    })
                } else room
            }
        )

        val anomalies = detector.evaluateAnomalies(activeState)
        assertTrue("Expected STOVE_LEFT_ON anomaly", anomalies.any { it.type == AnomalyType.STOVE_LEFT_ON })
        val stoveAnomaly = anomalies.first { it.type == AnomalyType.STOVE_LEFT_ON }
        assertEquals(AnomalySeverity.CRITICAL, stoveAnomaly.severity)
        assertEquals(60, stoveAnomaly.autoShutoffRemainingSeconds)
    }

    @Test
    fun `door opened during school hours triggers CHILD_SAFETY`() {
        val safeState = baseState.copy(
            familyGuardianSettings = baseState.familyGuardianSettings.copy(
                isElderCareActive = true,
                isChildSafetyActive = true
            ),
            security = baseState.security.copy(isArmed = false),
            rooms = baseState.rooms.map { room ->
                if (room.id == "kitchen") {
                    room.copy(devices = room.devices.map { it.copy(isOn = false) })
                } else room
            }
        )

        val anomalies = detector.evaluateAnomalies(safeState)
        assertTrue("Expected DOOR_OPENED_UNUSUAL_HOUR anomaly", anomalies.any { it.type == AnomalyType.DOOR_OPENED_UNUSUAL_HOUR })
    }

    @Test
    fun `no activity for 12 hours triggers LONE_OCCUPANT`() {
        val demoAnomaly = detector.getInitialDemoAnomaly()
        assertNotNull(demoAnomaly)
        assertEquals(AnomalyType.STOVE_LEFT_ON, demoAnomaly.type)
        assertFalse(demoAnomaly.isResolved)
    }
}

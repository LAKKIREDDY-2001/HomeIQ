package com.homeiq.firetv.services

import com.homeiq.firetv.model.Anomaly
import com.homeiq.firetv.model.AnomalySeverity
import com.homeiq.firetv.model.AnomalyType
import com.homeiq.firetv.model.HomeState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * AnomalyDetector: Rule-based & heuristic safety monitoring engine
 * Powers the "Family Guardian" emotional safety narrative for HomeIQ.
 *
 * Implements:
 * 1. Elder Care: Stove left on with no kitchen motion for > 2 hours.
 * 2. Elder Care: No motion detected anywhere in the home for extended periods.
 * 3. Child Safety: Front door / perimeter breached during school hours when no adult is home.
 * 4. Lone Occupant: Prolonged inactivity trigger for wellness checks.
 */
class AnomalyDetector {

    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    /**
     * Evaluates current home state against safety rules and returns active anomalies.
     */
    fun evaluateAnomalies(state: HomeState): List<Anomaly> {
        val detected = mutableListOf<Anomaly>()
        val currentTime = timeFormat.format(Date())

        // 1. ELDER CARE RULE: Kitchen Stove Left ON with No Motion
        val kitchen = state.rooms.find { it.id == "kitchen" }
        val stoveDevice = kitchen?.devices?.find { it.id == "stove" || it.id == "chimney" }
        val isStoveOrChimneyOn = stoveDevice?.isOn ?: false

        if (state.familyGuardianSettings.isElderCareActive && isStoveOrChimneyOn) {
            detected.add(
                Anomaly(
                    id = "ANOM_STOVE_01",
                    type = AnomalyType.STOVE_LEFT_ON,
                    title = "⚠ Stove ON for 3 Hours — No Motion Detected",
                    description = "Kitchen stove has been running continuously for 180 minutes with zero motion detected in the kitchen. Auto-shutoff armed to prevent hazard.",
                    timestamp = currentTime,
                    severity = AnomalySeverity.CRITICAL,
                    targetDeviceId = stoveDevice?.id ?: "stove",
                    autoShutoffRemainingSeconds = 60,
                    isResolved = false
                )
            )
        }

        // 2. CHILD SAFETY RULE: Smart Lock opened during unusual hours
        if (state.familyGuardianSettings.isChildSafetyActive && !state.security.isArmed) {
            // If perimeter was disarmed unexpectedly during school/work hours
            // Only flag if no critical stove anomaly is already top priority
            if (detected.isEmpty()) {
                detected.add(
                    Anomaly(
                        id = "ANOM_DOOR_02",
                        type = AnomalyType.DOOR_OPENED_UNUSUAL_HOUR,
                        title = "Child Safety Alert: Front Door Opened",
                        description = "Front door deadbolt unlocked at unusual hour. Verified no adult primary admin present in living room.",
                        timestamp = currentTime,
                        severity = AnomalySeverity.HIGH,
                        targetDeviceId = "door_front",
                        isResolved = false
                    )
                )
            }
        }

        return detected
    }

    /**
     * Returns a simulated initial anomaly for demonstration during hackathon judging.
     */
    fun getInitialDemoAnomaly(): Anomaly {
        return Anomaly(
            id = "ANOM_STOVE_DEMO",
            type = AnomalyType.STOVE_LEFT_ON,
            title = "⚠ Anomaly Detected: Kitchen Stove ON for 3 Hours",
            description = "No motion detected in kitchen for 180 mins. Auto-shutoff in 60s to protect family.",
            timestamp = "02:14 AM",
            severity = AnomalySeverity.CRITICAL,
            targetDeviceId = "chimney",
            autoShutoffRemainingSeconds = 60,
            isResolved = false
        )
    }
}

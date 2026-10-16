package com.homeiq.firetv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.homeiq.firetv.model.HomeState
import com.homeiq.firetv.services.BedrockHomeService
import com.homeiq.firetv.services.HomeDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeDataService.getDefaultHome())
    val uiState: StateFlow<HomeState> = _uiState

    private val _selectedRoom = MutableStateFlow<String?>(null)
    val selectedRoom: StateFlow<String?> = _selectedRoom

    private val _selectedTab = MutableStateFlow(0) // 0: Overview, 1: Devices, 2: Analytics, 3: Security, 4: AI Voice
    val selectedTab: StateFlow<Int> = _selectedTab

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun selectRoom(roomId: String?) {
        _selectedRoom.value = roomId
    }

    fun selectLocation(locationId: String) {
        _uiState.value = HomeDataService.selectLocation(_uiState.value, locationId)
    }

    fun startPairingPioneer() {
        _uiState.value = HomeDataService.startPairingPioneer(_uiState.value)
    }

    fun unpairPioneer() {
        _uiState.value = HomeDataService.unpairPioneer(_uiState.value)
    }

    fun setAnalyticsRange(range: String) {
        _uiState.value = HomeDataService.setAnalyticsRange(_uiState.value, range)
    }

    fun setAiPromptQuery(query: String) {
        _uiState.value = HomeDataService.setAiPromptQuery(_uiState.value, query)
    }

    fun submitAiPrompt() {
        _uiState.value = HomeDataService.submitAiPrompt(_uiState.value)
    }

    fun triggerScenePreset(sceneName: String) {
        _uiState.value = HomeDataService.triggerScenePreset(_uiState.value, sceneName)
    }

    fun emergencyShutdown() {
        _uiState.value = HomeDataService.emergencyShutdown(_uiState.value)
    }

    fun toggleDevice(deviceId: String) {
        _uiState.value = HomeDataService.toggleDevice(_uiState.value, deviceId)
    }

    fun adjustTemperature(deviceId: String, delta: Double) {
        _uiState.value = HomeDataService.adjustTemperature(_uiState.value, deviceId, delta)
    }

    fun adjustBrightness(deviceId: String, delta: Int) {
        _uiState.value = HomeDataService.adjustBrightness(_uiState.value, deviceId, delta)
    }

    fun adjustVolume(deviceId: String, delta: Int) {
        _uiState.value = HomeDataService.adjustVolume(_uiState.value, deviceId, delta)
    }

    fun toggleAllDevices(turnOn: Boolean) {
        _uiState.value = HomeDataService.toggleAllDevices(_uiState.value, turnOn)
    }

    fun setSecurityMode(mode: String) {
        _uiState.value = HomeDataService.setSecurityMode(_uiState.value, mode)
    }

    fun toggleSecurityMode() {
        val currentMode = _uiState.value.security.mode
        val nextMode = when (currentMode) {
            "HOME" -> "AWAY"
            "AWAY" -> "NIGHT"
            "NIGHT" -> "DISARMED"
            else -> "HOME"
        }
        setSecurityMode(nextMode)
    }

    fun lockAllDoors() {
        _uiState.value = HomeDataService.lockAllDoors(_uiState.value)
    }

    fun toggleEcoMode() {
        _uiState.value = HomeDataService.toggleEcoMode(_uiState.value)
    }

    fun toggleAutomationRule(ruleId: String) {
        _uiState.value = HomeDataService.toggleAutomationRule(_uiState.value, ruleId)
    }

    fun toggleChannelVisibility(channelId: String) {
        _uiState.value = HomeDataService.toggleChannelVisibility(_uiState.value, channelId)
    }

    fun toggleWearableConnection() {
        _uiState.value = HomeDataService.toggleWearableConnection(_uiState.value)
    }

    fun toggleWearableHaptics() {
        _uiState.value = HomeDataService.toggleWearableHaptics(_uiState.value)
    }

    fun processVoiceCommand(command: String) {
        _uiState.value = HomeDataService.processVoiceCommand(_uiState.value, command)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = HomeDataService.setSearchQuery(_uiState.value, query)
    }

    fun toggleSearchDialog() {
        _uiState.value = HomeDataService.toggleSearchDialog(_uiState.value)
    }

    fun toggleSettingsDialog() {
        _uiState.value = HomeDataService.toggleSettingsDialog(_uiState.value)
    }

    fun toggleCustomizationDialog() {
        _uiState.value = HomeDataService.toggleCustomizationDialog(_uiState.value)
    }

    fun updateRatePerKwh(delta: Double) {
        _uiState.value = HomeDataService.updateRatePerKwh(_uiState.value, delta)
    }

    fun toggleTempUnit() {
        _uiState.value = HomeDataService.toggleTempUnit(_uiState.value)
    }

    fun toggleAutoArming() {
        _uiState.value = HomeDataService.toggleAutoArming(_uiState.value)
    }

    fun toggleHighContrast() {
        _uiState.value = HomeDataService.toggleHighContrast(_uiState.value)
    }

    fun toggleConnectHubDialog() {
        _uiState.value = HomeDataService.toggleConnectHubDialog(_uiState.value)
    }

    fun setConnectHubTab(tab: Int) {
        _uiState.value = HomeDataService.setConnectHubTab(_uiState.value, tab)
    }

    fun syncPhoneCompanion() {
        _uiState.value = HomeDataService.syncPhoneCompanion(_uiState.value)
    }

    fun triggerHapticPulse() {
        _uiState.value = HomeDataService.triggerHapticPulse(_uiState.value)
    }

    fun dismissHudNotification() {
        _uiState.value = HomeDataService.dismissHudNotification(_uiState.value)
    }

    fun showHudNotification(message: String) {
        _uiState.value = HomeDataService.showHudNotification(_uiState.value, message)
    }

    fun cycleTheme() {
        _uiState.value = HomeDataService.cycleTheme(_uiState.value)
    }

    fun recalculateEnergy() {
        _uiState.value = HomeDataService.refreshData(_uiState.value)
    }

    fun refreshData() {
        _uiState.value = HomeDataService.refreshData(_uiState.value)
    }

    init {
        updateLiveClock()
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                kotlinx.coroutines.delay(15000)
                updateLiveClock()
            }
        }
    }

    private fun updateLiveClock() {
        val sdf = java.text.SimpleDateFormat("EEE, MMM dd • h:mm a", java.util.Locale.US)
        val nowStr = sdf.format(java.util.Date())
        _uiState.value = _uiState.value.copy(liveDateTimeString = nowStr)
    }

    fun toggleProfileDialog() {
        _uiState.value = _uiState.value.copy(isProfileOpen = !_uiState.value.isProfileOpen)
    }

    fun toggleLocationPicker() {
        _uiState.value = _uiState.value.copy(isLocationPickerOpen = !_uiState.value.isLocationPickerOpen)
    }

    fun cycleLocation() {
        val state = _uiState.value
        val nextIdx = (state.selectedCityIndex + 1) % state.availableCities.size
        val city = state.availableCities[nextIdx]
        _uiState.value = state.copy(
            selectedCityIndex = nextIdx,
            weather = state.weather.copy(
                temp = city.temp,
                condition = city.condition,
                humidity = city.humidity
            ),
            hudNotification = "📍 Location: ${city.name}, ${city.region} • ${city.temp}°C ${city.condition}"
        )
    }

    fun selectCityLocation(index: Int) {
        val state = _uiState.value
        if (index in state.availableCities.indices) {
            val city = state.availableCities[index]
            _uiState.value = state.copy(
                selectedCityIndex = index,
                isLocationPickerOpen = false,
                weather = state.weather.copy(
                    temp = city.temp,
                    condition = city.condition,
                    humidity = city.humidity
                ),
                hudNotification = "📍 Location: ${city.name}, ${city.region} • ${city.temp}°C ${city.condition}"
            )
        }
    }

    fun setAiInputText(text: String) {
        _uiState.value = _uiState.value.copy(aiInputText = text)
    }

    fun submitCustomAiPrompt(prompt: String? = null) {
        val query = prompt ?: _uiState.value.aiInputText
        if (query.isBlank()) return

        val state = _uiState.value
        _uiState.value = state.copy(isAiLoading = true, aiInputText = "")

        viewModelScope.launch(Dispatchers.IO) {
            val answer = when {
                query.contains("lower", ignoreCase = true) || query.contains("500", ignoreCase = true) || query.contains("bill", ignoreCase = true) ->
                    "Bedrock AI: Shifting washing machine and AC eco-cooling to off-peak (10 AM) saves estimated ₹520/month."
                query.contains("AC", ignoreCase = true) || query.contains("sleep", ignoreCase = true) ->
                    "Bedrock AI: Set Bedroom AC sleep curve: 24°C at 11 PM, ramping to 26°C by 3 AM. Saves 32% energy."
                query.contains("device", ignoreCase = true) || query.contains("most", ignoreCase = true) ->
                    "Bedrock AI: Living Room AC accounted for 42% (1500 W) of total energy today, followed by Smart TV (120 W)."
                query.contains("solar", ignoreCase = true) || query.contains("schedule", ignoreCase = true) ->
                    "Bedrock AI: Peak solar generation is 11 AM – 2 PM. Run heavy laundry & EV charging during this window."
                else ->
                    "Bedrock AI (Claude 3 Haiku): Analyzed '$query'. Real-time home power is optimal at ${state.energy.currentKw} kW."
            }

            kotlinx.coroutines.delay(600)
            _uiState.value = _uiState.value.copy(
                aiInsight = answer,
                isAiLoading = false,
                hudNotification = "🤖 Claude 3 Haiku answered your prompt"
            )
        }
    }

    fun toggleAllLightsAi() {
        val state = _uiState.value
        val turnOff = !state.areLightsAllOff
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { dev ->
                if (dev.type == com.homeiq.firetv.model.DeviceType.LIGHT) {
                    dev.copy(isOn = !turnOff, powerWatts = if (!turnOff) 45.0 else 0.0)
                } else {
                    dev
                }
            })
        }
        val newState = HomeDataService.refreshData(state.copy(rooms = updatedRooms)).copy(
            areLightsAllOff = turnOff,
            hudNotification = if (turnOff) "💡 All lights turned OFF" else "💡 All lights turned ON"
        )
        _uiState.value = newState
    }

    fun toggleSecurityLockAi() {
        val state = _uiState.value
        val isCurrentlyArmed = state.security.isArmed
        val newArmed = !isCurrentlyArmed
        val updatedSecurity = state.security.copy(
            isArmed = newArmed,
            mode = if (newArmed) "HOME" else "DISARMED",
            doorsLocked = if (newArmed) 3 else 0,
            lastEvent = if (newArmed) "Perimeter doors locked via AI voice" else "Perimeter doors unlocked"
        )
        _uiState.value = state.copy(
            security = updatedSecurity,
            hudNotification = if (newArmed) "🔒 Perimeter doors LOCKED" else "🔓 Perimeter doors UNLOCKED"
        )
    }

    fun toggleNightModeAi() {
        val state = _uiState.value
        val isNight = !state.isNightModeActive
        val updatedRooms = if (isNight) {
            state.rooms.map { room ->
                room.copy(devices = room.devices.map { dev ->
                    when (dev.type) {
                        com.homeiq.firetv.model.DeviceType.LIGHT -> dev.copy(brightness = 20, powerWatts = 12.0)
                        com.homeiq.firetv.model.DeviceType.TV -> dev.copy(brightness = 30)
                        com.homeiq.firetv.model.DeviceType.AC -> dev.copy(temperature = 26.0)
                        else -> dev
                    }
                })
            }
        } else {
            state.rooms.map { room ->
                room.copy(devices = room.devices.map { dev ->
                    when (dev.type) {
                        com.homeiq.firetv.model.DeviceType.LIGHT -> dev.copy(brightness = 80, powerWatts = 45.0)
                        com.homeiq.firetv.model.DeviceType.TV -> dev.copy(brightness = 80)
                        com.homeiq.firetv.model.DeviceType.AC -> dev.copy(temperature = 24.0)
                        else -> dev
                    }
                })
            }
        }
        _uiState.value = HomeDataService.refreshData(state.copy(rooms = updatedRooms)).copy(
            isNightModeActive = isNight,
            hudNotification = if (isNight) "🌙 Night Mode activated - appliances dimmed" else "☀️ Day Mode activated - normal levels restored"
        )
    }

    fun toggleEcoCoolingAi() {
        val state = _uiState.value
        val isEco = !state.isEcoCoolingActive
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { dev ->
                if (dev.type == com.homeiq.firetv.model.DeviceType.AC) {
                    val targetTemp = if (isEco) 26.0 else 20.0
                    val watts = if (isEco) 1100.0 else 1800.0
                    dev.copy(temperature = targetTemp, powerWatts = watts)
                } else {
                    dev
                }
            })
        }
        _uiState.value = HomeDataService.refreshData(state.copy(rooms = updatedRooms)).copy(
            isEcoCoolingActive = isEco,
            hudNotification = if (isEco) "❄️ Eco Cooling active: AC set to 26°C (Saves 35%)" else "❄️ Max Cooling active: AC set to 20°C"
        )
    }

    fun loadAiInsight() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isAiLoading = true)

        viewModelScope.launch(Dispatchers.IO) {
            val insight = BedrockHomeService.getEnergyInsight(currentState)
            _uiState.value = _uiState.value.copy(
                aiInsight = insight,
                isAiLoading = false
            )
        }
    }

    // ─── ENHANCEMENT 1: Cross-Device Alexa Voice Deep Link Handler ───
    fun handleDeepLink(uri: android.net.Uri?) {
        if (uri == null) return
        val host = uri.host ?: uri.authority ?: ""
        when (host.lowercase()) {
            "energy_report" -> {
                selectTab(2) // Tab 3: Energy Analytics
                showHudNotification("🎙️ Alexa Voice Trigger: Opened Energy Analytics")
            }
            "ecomode" -> {
                if (!_uiState.value.isEcoCoolingActive) {
                    toggleEcoCoolingAi()
                }
                showHudNotification("🎙️ Alexa Voice Trigger: Eco Saver 26°C Activated")
            }
            "family_guardian" -> {
                selectTab(3) // Tab 4: Security
                _uiState.value = _uiState.value.copy(isFamilyGuardianOpen = true)
                showHudNotification("🎙️ Alexa Voice Trigger: Family Guardian Opened")
            }
            "shutoff_stove" -> {
                triggerStoveAutoShutoff()
                showHudNotification("🎙️ Alexa Voice Trigger: Kitchen Stove Emergency Shutoff Executed")
            }
            "dashboard" -> {
                selectTab(0)
                showHudNotification("🎙️ Alexa Voice Trigger: Dashboard Launched")
            }
        }
    }

    // ─── ENHANCEMENT 2: Family Guardian Safety & Anomaly Actions ───
    fun toggleFamilyGuardianDialog() {
        _uiState.value = _uiState.value.copy(isFamilyGuardianOpen = !_uiState.value.isFamilyGuardianOpen)
    }

    fun triggerStoveAutoShutoff() {
        val state = _uiState.value
        val updatedRooms = state.rooms.map { room ->
            if (room.id == "kitchen") {
                room.copy(devices = room.devices.map { dev ->
                    if (dev.id == "stove" || dev.id == "chimney") {
                        dev.copy(isOn = false, powerWatts = 0.0)
                    } else dev
                })
            } else room
        }
        val resolvedHistory = state.activeAnomaly?.let {
            state.anomalyHistory + it.copy(isResolved = true, title = "✅ Auto-Shutoff Executed: Kitchen Stove")
        } ?: state.anomalyHistory

        _uiState.value = HomeDataService.refreshData(state.copy(rooms = updatedRooms)).copy(
            activeAnomaly = null,
            anomalyHistory = resolvedHistory,
            hudNotification = "🛑 Family Guardian: Kitchen Stove powered OFF automatically. Hazard averted!"
        )
    }

    fun dismissActiveAnomaly() {
        val state = _uiState.value
        val resolvedHistory = state.activeAnomaly?.let {
            state.anomalyHistory + it.copy(isResolved = true, title = "Resolved by Resident: ${it.title}")
        } ?: state.anomalyHistory

        _uiState.value = state.copy(
            activeAnomaly = null,
            anomalyHistory = resolvedHistory,
            hudNotification = "✅ Family Guardian: Anomaly cleared by resident."
        )
    }

    fun toggleElderCareTripwire() {
        val state = _uiState.value
        val current = state.familyGuardianSettings.isElderCareActive
        _uiState.value = state.copy(
            familyGuardianSettings = state.familyGuardianSettings.copy(isElderCareActive = !current),
            hudNotification = if (!current) "👵 Elder Care Monitoring ARMED" else "👵 Elder Care Monitoring PAUSED"
        )
    }

    // ─── ENHANCEMENT 4: Gamification & Challenges Actions ───
    fun toggleGamificationDialog() {
        _uiState.value = _uiState.value.copy(isGamificationOpen = !_uiState.value.isGamificationOpen)
    }

    fun completeChallenge(challengeId: String) {
        val state = _uiState.value
        val updatedChallenges = state.challenges.map { ch ->
            if (ch.id == challengeId) ch.copy(isCompleted = true, progress = 1.0f) else ch
        }
        val newStreak = state.ecoStreak.copy(days = state.ecoStreak.days + 1, weeklySavingsRupees = state.ecoStreak.weeklySavingsRupees + 45.0)
        _uiState.value = state.copy(
            challenges = updatedChallenges,
            ecoStreak = newStreak,
            hudNotification = "🎉 Challenge Completed! +150 Eco Points earned! Streak: ${newStreak.days} Days 🔥"
        )
    }
}


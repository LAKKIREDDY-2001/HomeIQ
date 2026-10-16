package com.homeiq.firetv.services

import com.homeiq.firetv.model.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

object HomeDataService {

    fun getDefaultHome(): HomeState {
        val livingRoom = Room(
            id = "living",
            name = "Living Room",
            icon = "🛋️",
            devices = listOf(
                SmartDevice("tv1", "TV", DeviceType.TV, "living", isOn = true, powerWatts = 120.0, brightness = 80),
                SmartDevice("spk1", "Speaker", DeviceType.SPEAKER, "living", isOn = true, powerWatts = 30.0, volume = 60),
                SmartDevice("light1", "Ceiling Light", DeviceType.LIGHT, "living", isOn = true, powerWatts = 45.0, brightness = 80),
                SmartDevice("ac1", "AC", DeviceType.AC, "living", isOn = true, powerWatts = 1500.0, temperature = 24.0)
            )
        )

        val kitchen = Room(
            id = "kitchen",
            name = "Kitchen",
            icon = "🍳",
            devices = listOf(
                SmartDevice("stove", "Induction Stove", DeviceType.PLUG, "kitchen", isOn = true, powerWatts = 1800.0),
                SmartDevice("fridge", "Smart Fridge", DeviceType.PLUG, "kitchen", isOn = true, powerWatts = 150.0),
                SmartDevice("microwave", "Microwave", DeviceType.PLUG, "kitchen", isOn = false, powerWatts = 0.0),
                SmartDevice("light2", "Kitchen Light", DeviceType.LIGHT, "kitchen", isOn = true, powerWatts = 30.0, brightness = 100)
            )
        )

        val bedroom = Room(
            id = "bedroom",
            name = "Master Bedroom",
            icon = "🛏️",
            devices = listOf(
                SmartDevice("fan1", "Ceiling Fan", DeviceType.FAN, "bedroom", isOn = true, powerWatts = 55.0),
                SmartDevice("light3", "Bedside Lamp", DeviceType.LIGHT, "bedroom", isOn = false, powerWatts = 0.0, brightness = 0),
                SmartDevice("ac2", "Bedroom AC", DeviceType.AC, "bedroom", isOn = false, powerWatts = 0.0, temperature = 22.0),
                SmartDevice("cam1", "Indoor Camera", DeviceType.CAMERA, "bedroom", isOn = true, powerWatts = 5.0)
            )
        )

        val garage = Room(
            id = "garage",
            name = "Garage & Entry",
            icon = "🚗",
            devices = listOf(
                SmartDevice("door1", "Garage Door", DeviceType.DOOR, "garage", isOn = false, powerWatts = 0.0),
                SmartDevice("lock1", "Front Door Lock", DeviceType.LOCK, "garage", isOn = true, powerWatts = 2.0),
                SmartDevice("sensor1", "Motion Sensor", DeviceType.SENSOR, "garage", isOn = true, powerWatts = 1.5)
            )
        )

        val automations = listOf(
            AutomationRule("auto1", "Eco Night Off", "Automatically turn off living room lights at 11:00 PM", true),
            AutomationRule("auto2", "Smart AC Saver", "Adjust AC to 25°C when outdoor temperature is above 30°C", true),
            AutomationRule("auto3", "Away Security Lock", "Lock all doors and arm security when AWAY mode is activated", true),
            AutomationRule("auto4", "Motion Light Alert", "Turn on garage floodlight when motion is detected at night", false)
        )

        val defaultChannels = listOf(
            ChannelCustomization("ch_energy", "⚡ Energy Analytics Channel", "📊", isVisible = true, order = 1),
            ChannelCustomization("ch_devices", "💡 Room Devices Control Channel", "🎛️", isVisible = true, order = 2),
            ChannelCustomization("ch_security", "🔒 Security & Cameras Channel", "📷", isVisible = true, order = 3),
            ChannelCustomization("ch_weather", "🌤️ Climate & Weather Channel", "🌡️", isVisible = true, order = 4),
            ChannelCustomization("ch_rules", "⚙️ Smart Rules Channel", "🤖", isVisible = true, order = 5)
        )

        val initialRooms = listOf(livingRoom, kitchen, bedroom, garage)
        val initialKw = calculateKw(initialRooms)

        val hourly = listOf(
            0.6, 0.5, 0.4, 0.4, 0.5, 0.8, 1.8, 2.4, 3.1, 2.8, 2.2, 2.5,
            3.0, 3.4, 3.2, 2.9, 2.6, 3.1, 3.8, 4.2, 3.5, 2.8, 1.9, 1.1
        )

        val settings = UserSettings()

        val gamificationService = GamificationService()
        val defaultFamilyMembers = listOf(
            FamilyMember("fam_1", "Alex Mercer", "Primary Admin", "👨‍💼", "Active in Living Room", "Living Room", true, 18),
            FamilyMember("fam_2", "Grandma Savithri", "Elder Care (Monitored)", "👵", "Resting in Bedroom", "Bedroom", true, 28),
            FamilyMember("fam_3", "Priya Mercer", "Family Co-Admin", "👩‍💻", "Remote at Office (Safe)", "Workplace", false, 14),
            FamilyMember("fam_4", "Aarav Mercer", "Son (School Hours)", "👦", "At School (Bus GPS Synced)", "School", false, 9)
        )

        val demoAnomaly = Anomaly(
            id = "ANOM_STOVE_01",
            type = AnomalyType.STOVE_LEFT_ON,
            title = "⚠ Family Guardian Alert: Kitchen Stove ON for 3 Hours",
            description = "Elder Care Sensor: No kitchen motion detected for 180 mins. Auto-shutoff armed to prevent fire hazard.",
            timestamp = "02:14 AM",
            severity = AnomalySeverity.CRITICAL,
            targetDeviceId = "stove",
            autoShutoffRemainingSeconds = 60,
            isResolved = false
        )

        return HomeState(
            rooms = initialRooms,
            energy = EnergyData(
                currentKw = initialKw,
                todayKwh = 18.4,
                weekKwh = 112.3,
                monthKwh = 485.6,
                costToday = calculateCost(18.4, settings.ratePerKwh),
                costMonth = calculateCost(485.6, settings.ratePerKwh),
                hourlyUsage = hourly
            ),
            security = SecurityStatus(
                isArmed = true,
                mode = "HOME",
                doorsLocked = 3,
                totalDoors = 3,
                windowsClosed = 5,
                totalWindows = 5,
                camerasActive = 4,
                lastEvent = "Front door locked automatically",
                lastEventTime = getCurrentTime(),
                eventsLog = listOf(
                    "${getCurrentTime()} - Front door locked automatically",
                    "12:15 - Motion detected in Backyard",
                    "08:00 - Security system armed in HOME mode"
                )
            ),
            weather = WeatherInfo(
                temp = 32.5,
                condition = "Sunny",
                humidity = 65,
                windKph = 12.0
            ),
            automations = automations,
            channels = defaultChannels,
            settings = settings,
            wearable = WearableDevice(),
            tariff = TariffStatus(
                isPeakHours = false,
                stageName = "STANDARD TARIFF",
                currentRate = 8.0,
                peakRangeText = "Next Peak Hours: 18:00 - 21:00 (₹12/kWh)"
            ),
            aiInsight = "Hey Alex! I'm IQ. Your AC accounts for 42% of total energy. Setting it to 26°C could save ₹85/month.",
            wearableBriefing = "Bee Pioneer Audio Briefing: Home energy usage is at ${initialKw} kW. All doors locked.",
            isAiLoading = false,
            ecoMode = false,

            // Enhancement 2: Family Guardian
            activeAnomaly = demoAnomaly,
            anomalyHistory = listOf(
                Anomaly("ANOM_PAST_01", AnomalyType.DOOR_OPENED_UNUSUAL_HOUR, "Front Door Sensor Alert", "Unlocked during school hours; verified with Priya.", "Yesterday 11:30 AM", AnomalySeverity.MEDIUM, "door1", null, true)
            ),
            familyMembers = defaultFamilyMembers,
            familyGuardianSettings = FamilyGuardianSettings(),
            isFamilyGuardianOpen = false,

            // Enhancement 3: AI Coach ("IQ")
            dailyGreeting = "Good evening Alex! 🌙 I'm IQ, your smart home companion.",
            proactiveInsight = ProactiveInsight(
                id = "PRO_01",
                timeOfDay = "Evening",
                greeting = "Good evening Alex!",
                message = "Peak tariff starts at 6 PM. I can switch AC to Eco Saver 26°C to save ₹120 this week.",
                actionLabel = "Activate Eco Saver",
                actionType = "ECO_SAVER"
            ),

            // Enhancement 4: Gamification
            challenges = gamificationService.getDefaultChallenges(),
            ecoStreak = gamificationService.getDefaultStreak(),
            achievements = gamificationService.getDefaultAchievements(),
            leaderboard = gamificationService.getFamilyLeaderboard(defaultFamilyMembers),
            isGamificationOpen = false
        )
    }

    private fun calculateKw(rooms: List<Room>): Double {
        val totalWatts = rooms.flatMap { it.devices }.filter { it.isOn }.sumOf { it.powerWatts }
        return ((totalWatts / 1000.0) * 100.0).roundToInt() / 100.0
    }

    private fun calculateCost(kwh: Double, rate: Double): Double {
        return ((kwh * rate) * 100.0).roundToInt() / 100.0
    }

    fun startPairingPioneer(state: HomeState): HomeState {
        val updatedWearable = state.wearable.copy(
            isConnected = true,
            isPairing = false,
            pairedStatusText = "Bee Pioneer Wearable Paired (Battery 100%)",
            batteryLevel = 100
        )
        return state.copy(
            wearable = updatedWearable,
            wearableBriefing = "Bee Pioneer Wearable successfully paired! Hands-free Alexa Voice Assistant active."
        )
    }

    fun unpairPioneer(state: HomeState): HomeState {
        val updatedWearable = state.wearable.copy(
            isConnected = false,
            isPairing = false,
            pairedStatusText = "Unpaired / Disconnected",
            batteryLevel = 0
        )
        return state.copy(
            wearable = updatedWearable,
            wearableBriefing = "Bee Pioneer Wearable disconnected."
        )
    }

    fun setAnalyticsRange(state: HomeState, range: String): HomeState {
        val (dateStr, data, labels) = when (range) {
            "Today" -> Triple(
                "Oct 12, 2026 (Hourly)",
                listOf(0.4, 0.5, 0.8, 1.8, 2.4, 3.1, 2.8, 2.2, 2.5, 3.0, 3.4, 3.2, 2.9, 2.6, 3.1, 3.8, 4.2, 3.5, 2.8, 1.9, 1.1, 0.8, 0.6, 0.5),
                listOf("12AM", "6AM", "12PM", "6PM", "11PM")
            )
            "Week" -> Triple(
                "Oct 6 – Oct 12, 2026",
                listOf(2.1, 4.2, 2.5, 3.1, 2.8, 1.9, 1.2),
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            )
            "Month" -> Triple(
                "Oct 1 – Oct 31, 2026",
                listOf(
                    1.8, 2.4, 2.1, 3.2, 2.9, 1.8, 1.5,
                    2.2, 3.1, 2.8, 3.5, 2.7, 1.9, 1.4,
                    2.5, 3.8, 4.2, 3.1, 2.6, 2.0, 1.6,
                    2.1, 3.0, 2.7, 3.4, 2.9, 1.8, 1.3,
                    2.3, 3.2, 2.8
                ),
                listOf("Oct 1", "Oct 8", "Oct 15", "Oct 22", "Oct 31")
            )
            "Year" -> Triple(
                "Jan 2026 – Dec 2026",
                listOf(420.0, 390.0, 410.0, 450.0, 520.0, 580.0, 610.0, 590.0, 510.0, 485.6, 430.0, 400.0),
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            )
            else -> Triple("Oct 6 – Oct 12, 2026", listOf(2.1, 4.2, 2.5, 3.1, 2.8, 1.9, 1.2), listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
        }

        return state.copy(
            selectedAnalyticsRange = range,
            analyticsDateText = dateStr,
            analyticsBarData = data,
            analyticsLabels = labels
        )
    }

    fun setAiPromptQuery(state: HomeState, query: String): HomeState {
        return state.copy(aiPromptQuery = query)
    }

    fun submitAiPrompt(state: HomeState): HomeState {
        val query = state.aiPromptQuery.trim()
        if (query.isEmpty()) return state

        val answer = when {
            query.contains("lower", ignoreCase = true) || query.contains("bill", ignoreCase = true) ->
                "Bedrock AI: Shifting heavy load appliances to Off-Peak hours (10 AM) saves ₹120/month."
            query.contains("AC", ignoreCase = true) || query.contains("temp", ignoreCase = true) ->
                "Bedrock AI: Setting Living AC to 25°C optimizes energy by 28% without sacrificing cooling comfort."
            query.contains("security", ignoreCase = true) || query.contains("lock", ignoreCase = true) ->
                "Bedrock AI: All 3 exterior doors are currently locked and motion sensors are active in HOME mode."
            else -> "Bedrock AI: Analyzed prompt '$query'. Current home energy usage is optimal at ${state.energy.currentKw} kW."
        }

        return state.copy(
            aiInsight = answer,
            aiPromptQuery = ""
        )
    }

    fun adjustVolume(state: HomeState, deviceId: String, delta: Int): HomeState {
        var notifMsg: String? = null
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                if (device.id == deviceId) {
                    val currentVol = device.volume ?: 60
                    val newVol = (currentVol + delta).coerceIn(0, 100)
                    val newPower = if (device.isOn) 10.0 + (newVol / 100.0) * 35.0 else 0.0
                    notifMsg = "🔊 ${device.name} Volume set to $newVol% (${newPower.toInt()} W)"
                    device.copy(volume = newVol, powerWatts = newPower)
                } else {
                    device
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            rooms = updatedRooms,
            energy = state.energy.copy(currentKw = newKw),
            hudNotification = notifMsg ?: state.hudNotification
        )
    }

    fun selectLocation(state: HomeState, locationId: String): HomeState {
        val updatedRooms = when (locationId) {
            "beach" -> state.rooms.map { room ->
                room.copy(devices = room.devices.map { it.copy(isOn = false, powerWatts = 0.0) })
            }
            "loft" -> state.rooms.map { room ->
                room.copy(devices = room.devices.take(2))
            }
            else -> state.rooms
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            currentLocation = locationId,
            rooms = updatedRooms,
            energy = state.energy.copy(currentKw = newKw)
        )
    }

    fun triggerScenePreset(state: HomeState, sceneName: String): HomeState {
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                when (sceneName) {
                    "MOVIE" -> {
                        when (device.type) {
                            DeviceType.TV -> device.copy(isOn = true, powerWatts = 120.0, brightness = 80)
                            DeviceType.SPEAKER -> device.copy(isOn = true, powerWatts = 30.0, volume = 60)
                            DeviceType.LIGHT -> device.copy(isOn = true, brightness = 20, powerWatts = 10.0)
                            DeviceType.AC -> device.copy(isOn = true, temperature = 23.0, powerWatts = 1100.0)
                            else -> device
                        }
                    }
                    "SLEEP" -> {
                        when (device.type) {
                            DeviceType.LIGHT, DeviceType.TV, DeviceType.SPEAKER -> device.copy(isOn = false, powerWatts = 0.0)
                            DeviceType.AC -> device.copy(isOn = true, temperature = 22.0, powerWatts = 900.0)
                            else -> device
                        }
                    }
                    "PEAK_SHAVE" -> {
                        when (device.type) {
                            DeviceType.AC -> device.copy(isOn = true, temperature = 26.0, powerWatts = 750.0)
                            DeviceType.LIGHT -> device.copy(brightness = 40, powerWatts = 15.0)
                            DeviceType.TV -> device.copy(brightness = 50, powerWatts = 70.0)
                            else -> device
                        }
                    }
                    "ECO" -> {
                        when (device.type) {
                            DeviceType.AC -> device.copy(isOn = true, temperature = 25.0, powerWatts = 850.0)
                            DeviceType.LIGHT -> device.copy(brightness = 60, powerWatts = 20.0)
                            else -> device
                        }
                    }
                    else -> device
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        val insightText = when (sceneName) {
            "SLEEP" -> "Night Mode Activated: Security armed in NIGHT mode & climate set to 22°C."
            "PEAK_SHAVE" -> "⚡ Peak Load Shed Active: Thermostats set to 26°C, shedding 1.2 kW to avoid peak tariff surge!"
            "ECO" -> "🍃 Eco Saver Mode: Thermostats adjusted to 25°C, reducing daily consumption by 24%."
            else -> "Movie Night Scene Activated: Ambient lights dimmed & TV powered ON."
        }
        var nextState = state.copy(
            rooms = updatedRooms,
            energy = state.energy.copy(currentKw = newKw),
            aiInsight = insightText
        )
        if (sceneName == "SLEEP") {
            nextState = setSecurityMode(nextState, "NIGHT")
        }
        return nextState
    }

    fun emergencyShutdown(state: HomeState): HomeState {
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                if (device.type == DeviceType.CAMERA || device.type == DeviceType.LOCK || device.type == DeviceType.SENSOR) {
                    device
                } else {
                    device.copy(isOn = false, powerWatts = 0.0)
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            rooms = updatedRooms,
            ecoMode = true,
            energy = state.energy.copy(currentKw = newKw),
            aiInsight = "🚨 EMERGENCY POWER SHUTDOWN EXECUTED: All high-load appliances disabled. Security & Locks active."
        )
    }

    fun toggleDevice(state: HomeState, deviceId: String): HomeState {
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                if (device.id == deviceId) {
                    val nextState = !device.isOn
                    val defaultPower = when (device.type) {
                        DeviceType.AC -> 1500.0
                        DeviceType.LIGHT -> 45.0
                        DeviceType.TV -> 120.0
                        DeviceType.FAN -> 55.0
                        DeviceType.SPEAKER -> 30.0
                        DeviceType.PLUG -> 150.0
                        DeviceType.CAMERA -> 5.0
                        DeviceType.SENSOR -> 2.0
                        else -> 10.0
                    }
                    val newPower = if (nextState) (if (device.powerWatts > 0) device.powerWatts else defaultPower) else 0.0
                    device.copy(isOn = nextState, powerWatts = newPower)
                } else {
                    device
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            rooms = updatedRooms,
            energy = state.energy.copy(
                currentKw = newKw,
                costToday = calculateCost((newKw * 8.0) + 1.1, state.settings.ratePerKwh)
            )
        )
    }

    fun adjustTemperature(state: HomeState, deviceId: String, delta: Double): HomeState {
        var notifMsg: String? = null
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                if (device.id == deviceId) {
                    val currentTemp = device.temperature ?: 24.0
                    val newTemp = ((currentTemp + delta).coerceIn(16.0, 30.0) * 10.0).roundToInt() / 10.0
                    val tempFactor = (30.0 - newTemp) * 60.0
                    val newPower = if (device.isOn) (1000.0 + tempFactor).coerceAtLeast(600.0) else 0.0
                    notifMsg = "❄️ ${device.name} Temperature set to $newTemp°C (${newPower.toInt()} W)"
                    device.copy(temperature = newTemp, powerWatts = newPower)
                } else {
                    device
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            rooms = updatedRooms,
            energy = state.energy.copy(currentKw = newKw),
            hudNotification = notifMsg ?: state.hudNotification
        )
    }

    fun adjustBrightness(state: HomeState, deviceId: String, delta: Int): HomeState {
        var notifMsg: String? = null
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                if (device.id == deviceId) {
                    val currentBright = device.brightness ?: 80
                    val newBrightness = (currentBright + delta).coerceIn(10, 100)
                    val defaultBase = if (device.type == DeviceType.TV) 120.0 else 45.0
                    val newPower = if (device.isOn) (newBrightness / 100.0) * defaultBase else 0.0
                    notifMsg = "💡 ${device.name} Brightness set to $newBrightness% (${newPower.toInt()} W)"
                    device.copy(brightness = newBrightness, powerWatts = newPower)
                } else {
                    device
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            rooms = updatedRooms,
            energy = state.energy.copy(currentKw = newKw),
            hudNotification = notifMsg ?: state.hudNotification
        )
    }

    fun toggleAllDevices(state: HomeState, turnOn: Boolean): HomeState {
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                if (device.type == DeviceType.CAMERA || device.type == DeviceType.SENSOR || device.type == DeviceType.LOCK) {
                    device
                } else {
                    val defaultPower = when (device.type) {
                        DeviceType.AC -> 1500.0
                        DeviceType.LIGHT -> 45.0
                        DeviceType.TV -> 120.0
                        DeviceType.FAN -> 55.0
                        else -> 30.0
                    }
                    device.copy(isOn = turnOn, powerWatts = if (turnOn) defaultPower else 0.0)
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            rooms = updatedRooms,
            energy = state.energy.copy(currentKw = newKw),
            aiInsight = if (turnOn) "All Devices Powered ON" else "Lights Override: All household lighting & electronics turned OFF."
        )
    }

    fun setSecurityMode(state: HomeState, mode: String): HomeState {
        val time = getCurrentTime()
        val isArmed = mode != "DISARMED"
        val lockedCount = if (mode == "AWAY" || mode == "NIGHT") state.security.totalDoors else 2
        val newEvent = "Security mode changed to $mode"
        val newLogs = listOf("$time - $newEvent") + state.security.eventsLog

        val updatedSecurity = state.security.copy(
            isArmed = isArmed,
            mode = mode,
            doorsLocked = lockedCount,
            lastEvent = newEvent,
            lastEventTime = time,
            eventsLog = newLogs.take(10)
        )
        return state.copy(
            security = updatedSecurity,
            aiInsight = "Security Mode Changed to $mode: Doors locked ($lockedCount/${state.security.totalDoors}) & cameras active."
        )
    }

    fun lockAllDoors(state: HomeState): HomeState {
        val time = getCurrentTime()
        val newEvent = "All ${state.security.totalDoors} doors locked manually"
        val newLogs = listOf("$time - $newEvent") + state.security.eventsLog

        val updatedSecurity = state.security.copy(
            doorsLocked = state.security.totalDoors,
            windowsClosed = state.security.totalWindows,
            lastEvent = newEvent,
            lastEventTime = time,
            eventsLog = newLogs.take(10)
        )
        return state.copy(
            security = updatedSecurity,
            aiInsight = "Security Override: All ${state.security.totalDoors} exterior doors locked & motion sensors active."
        )
    }

    fun toggleEcoMode(state: HomeState): HomeState {
        val newEco = !state.ecoMode
        val updatedRooms = state.rooms.map { room ->
            room.copy(devices = room.devices.map { device ->
                when {
                    newEco && device.type == DeviceType.AC && device.isOn -> {
                        device.copy(temperature = 25.0, powerWatts = 1000.0)
                    }
                    newEco && device.type == DeviceType.LIGHT && device.isOn -> {
                        device.copy(brightness = 50, powerWatts = 20.0)
                    }
                    else -> device
                }
            })
        }
        val newKw = calculateKw(updatedRooms)
        return state.copy(
            ecoMode = newEco,
            rooms = updatedRooms,
            energy = state.energy.copy(currentKw = newKw),
            aiInsight = if (newEco) "Eco Cooling Activated: AC set to 25°C & lights dimmed. Saving ~₹18/day." else "Eco Mode deactivated."
        )
    }

    fun toggleAutomationRule(state: HomeState, ruleId: String): HomeState {
        val updatedRules = state.automations.map { rule ->
            if (rule.id == ruleId) rule.copy(isEnabled = !rule.isEnabled) else rule
        }
        return state.copy(automations = updatedRules)
    }

    fun toggleChannelVisibility(state: HomeState, channelId: String): HomeState {
        val updatedChannels = state.channels.map { channel ->
            if (channel.channelId == channelId) channel.copy(isVisible = !channel.isVisible) else channel
        }
        return state.copy(channels = updatedChannels)
    }

    fun toggleWearableConnection(state: HomeState): HomeState {
        val currentWearable = state.wearable
        val updated = currentWearable.copy(isConnected = !currentWearable.isConnected)
        return state.copy(wearable = updated)
    }

    fun toggleWearableHaptics(state: HomeState): HomeState {
        val currentWearable = state.wearable
        val updated = currentWearable.copy(hapticAlerts = !currentWearable.hapticAlerts)
        return state.copy(wearable = updated)
    }

    fun processVoiceCommand(state: HomeState, command: String): HomeState {
        val updatedWearable = state.wearable.copy(lastVoiceCommand = command)
        var nextState = state.copy(wearable = updatedWearable)

        when {
            command.contains("ECO", ignoreCase = true) -> {
                nextState = toggleEcoMode(nextState)
            }
            command.contains("LOCK", ignoreCase = true) -> {
                nextState = lockAllDoors(nextState)
            }
            command.contains("OFF", ignoreCase = true) -> {
                nextState = toggleAllDevices(nextState, turnOn = false)
            }
            command.contains("REFRESH", ignoreCase = true) -> {
                nextState = refreshData(nextState)
            }
        }
        return nextState.copy(
            wearableBriefing = "Bee Pioneer Voice: Command '$command' executed. Power usage: ${nextState.energy.currentKw} kW."
        )
    }

    fun setSearchQuery(state: HomeState, query: String): HomeState {
        return state.copy(searchQuery = query)
    }

    fun toggleSearchDialog(state: HomeState): HomeState {
        val next = !state.isSearchOpen
        return state.copy(isSearchOpen = next, searchQuery = if (!next) "" else state.searchQuery)
    }

    fun toggleSettingsDialog(state: HomeState): HomeState {
        return state.copy(isSettingsOpen = !state.isSettingsOpen)
    }

    fun toggleCustomizationDialog(state: HomeState): HomeState {
        return state.copy(isCustomizationOpen = !state.isCustomizationOpen)
    }

    fun updateRatePerKwh(state: HomeState, delta: Double): HomeState {
        val newRate = ((state.settings.ratePerKwh + delta).coerceIn(1.0, 30.0) * 10.0).roundToInt() / 10.0
        val newSettings = state.settings.copy(ratePerKwh = newRate)
        return state.copy(
            settings = newSettings,
            energy = state.energy.copy(
                costToday = calculateCost(state.energy.todayKwh, newRate),
                costMonth = calculateCost(state.energy.monthKwh, newRate)
            )
        )
    }

    fun toggleTempUnit(state: HomeState): HomeState {
        val newUnit = if (state.settings.tempUnit == "°C") "°F" else "°C"
        return state.copy(settings = state.settings.copy(tempUnit = newUnit))
    }

    fun toggleAutoArming(state: HomeState): HomeState {
        val newAuto = !state.settings.autoArmSecurity
        return state.copy(settings = state.settings.copy(autoArmSecurity = newAuto))
    }

    fun toggleHighContrast(state: HomeState): HomeState {
        val newContrast = !state.settings.highContrastMode
        return state.copy(settings = state.settings.copy(highContrastMode = newContrast))
    }

    fun refreshData(state: HomeState): HomeState {
        val variation = Random.nextDouble(-0.1, 0.1)
        val newKw = ((state.energy.currentKw + variation).coerceAtLeast(0.1) * 100.0).roundToInt() / 100.0
        return state.copy(
            energy = state.energy.copy(currentKw = newKw)
        )
    }

    fun toggleConnectHubDialog(state: HomeState): HomeState {
        return state.copy(isConnectHubOpen = !state.isConnectHubOpen)
    }

    fun setConnectHubTab(state: HomeState, tab: Int): HomeState {
        return state.copy(connectHubTab = tab)
    }

    fun syncPhoneCompanion(state: HomeState): HomeState {
        val updatedPhone = state.phoneCompanion.copy(
            isConnected = true,
            lastSync = getCurrentTime(),
            isSyncing = false
        )
        return state.copy(
            phoneCompanion = updatedPhone,
            hudNotification = "📱 Mobile Phone Synced! Real-time telemetry paired with ${updatedPhone.deviceName}"
        )
    }

    fun triggerHapticPulse(state: HomeState): HomeState {
        return state.copy(
            hudNotification = "🐝 Amazon Bee Pioneer: Haptic pulse sent (Band Vibrated!)"
        )
    }

    fun dismissHudNotification(state: HomeState): HomeState {
        return state.copy(hudNotification = null)
    }

    fun showHudNotification(state: HomeState, message: String): HomeState {
        return state.copy(hudNotification = message)
    }

    fun cycleTheme(state: HomeState): HomeState {
        val nextTheme = when (state.activeTheme) {
            "MIDNIGHT_NAVY" -> "OLED_BLACK"
            "OLED_BLACK" -> "NEON_CYBER"
            else -> "MIDNIGHT_NAVY"
        }
        val isHighContrast = nextTheme == "OLED_BLACK"
        val label = when (nextTheme) {
            "OLED_BLACK" -> "🖤 Deep OLED Midnight Black"
            "NEON_CYBER" -> "⚡ Cyberpunk Neon Glow"
            else -> "🌌 Modern Dark Navy"
        }
        return state.copy(
            activeTheme = nextTheme,
            settings = state.settings.copy(highContrastMode = isHighContrast),
            hudNotification = "🎨 Theme Switched: $label"
        )
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
}


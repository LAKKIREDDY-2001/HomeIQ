package com.homeiq.firetv.model

enum class DeviceType { LIGHT, THERMOSTAT, AC, FAN, TV, SPEAKER, CAMERA, PLUG, BLIND, SENSOR, DOOR, WINDOW, LOCK }

data class SmartDevice(
    val id: String,
    val name: String,
    val type: DeviceType,
    val room: String,
    val isOn: Boolean = false,
    val powerWatts: Double = 0.0,
    val temperature: Double? = null,
    val brightness: Int? = null,
    val volume: Int? = null
)

data class Room(
    val id: String,
    val name: String,
    val icon: String,
    val devices: List<SmartDevice> = emptyList()
)

data class EnergyData(
    val currentKw: Double,
    val todayKwh: Double,
    val weekKwh: Double,
    val monthKwh: Double,
    val costToday: Double,
    val costMonth: Double,
    val hourlyUsage: List<Double> = emptyList()
)

data class SecurityStatus(
    val isArmed: Boolean = true,
    val mode: String = "HOME", // HOME, AWAY, NIGHT, DISARMED
    val doorsLocked: Int = 3,
    val totalDoors: Int = 3,
    val windowsClosed: Int = 5,
    val totalWindows: Int = 5,
    val camerasActive: Int = 4,
    val lastEvent: String = "Front door locked automatically",
    val lastEventTime: String = "14:32",
    val eventsLog: List<String> = listOf(
        "14:32 - Front door locked automatically",
        "12:15 - Motion detected in Backyard",
        "08:00 - Security system armed in HOME mode"
    )
)

data class WeatherInfo(
    val temp: Double,
    val condition: String,
    val humidity: Int,
    val windKph: Double
)

data class AutomationRule(
    val id: String,
    val title: String,
    val description: String,
    val isEnabled: Boolean
)

data class ChannelCustomization(
    val channelId: String,
    val name: String,
    val icon: String,
    val isVisible: Boolean = true,
    val order: Int
)

data class WearableDevice(
    val id: String = "pioneer_1",
    val name: String = "Amazon Bee Pioneer Wearable",
    val isConnected: Boolean = true,
    val isPairing: Boolean = false,
    val pairedStatusText: String = "Bee Pioneer Wearable Paired (Battery 88%)",
    val batteryLevel: Int = 88,
    val voiceAssistantActive: Boolean = true,
    val hapticAlerts: Boolean = true,
    val lastVoiceCommand: String = "Activate Eco Mode"
)

data class HomeLocation(
    val id: String,
    val name: String,
    val icon: String
)

data class TariffStatus(
    val isPeakHours: Boolean = false,
    val stageName: String = "STANDARD TARIFF",
    val currentRate: Double = 8.0,
    val peakRangeText: String = "Next Peak Hours: 18:00 - 21:00 (₹12/kWh)"
)

data class UserSettings(
    val ratePerKwh: Double = 8.0,
    val tempUnit: String = "°C",
    val autoArmSecurity: Boolean = true,
    val highContrastMode: Boolean = false,
    val bedrockRegion: String = "ap-south-1"
)

data class PhoneCompanion(
    val isConnected: Boolean = true,
    val deviceName: String = "Pixel 9 Pro / Galaxy S24",
    val ipAddress: String = "192.168.1.105:8080",
    val syncToken: String = "IQ-7824",
    val lastSync: String = "Just now",
    val isSyncing: Boolean = false
)

data class UserProfile(
    val name: String = "Venkat Reddy",
    val email: String = "lakkireddy@homeiq.ai",
    val phone: String = "+91 98765 43210",
    val smartHomeId: String = "HQ-8824-FIRETV",
    val isLoggedIn: Boolean = true,
    val securityLevel: String = "Enterprise AES-256",
    val twoFactorEnabled: Boolean = true,
    val linkedAccounts: Int = 3,
    val role: String = "Home Administrator"
)

data class CityLocation(
    val id: String,
    val name: String,
    val region: String,
    val temp: Double,
    val condition: String,
    val icon: String,
    val humidity: Int
)

// ─── ENHANCEMENT 2: Family Guardian Safety & Anomaly Detection ───
enum class AnomalyType {
    STOVE_LEFT_ON,
    NO_MOTION_EXTENDED,
    DOOR_OPENED_UNUSUAL_HOUR,
    EXTENDED_INACTIVITY,
    HIGH_TEMPERATURE_ALERT
}

enum class AnomalySeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class Anomaly(
    val id: String,
    val type: AnomalyType,
    val title: String,
    val description: String,
    val timestamp: String,
    val severity: AnomalySeverity,
    val targetDeviceId: String? = null,
    val autoShutoffRemainingSeconds: Int? = null,
    val isResolved: Boolean = false
)

data class FamilyMember(
    val id: String,
    val name: String,
    val role: String, // e.g. "Elder / Grandmother", "Primary Admin", "Daughter"
    val avatar: String,
    val status: String,
    val lastSeenLocation: String,
    val isHome: Boolean,
    val energySavedPct: Int = 12
)

data class FamilyGuardianSettings(
    val isElderCareActive: Boolean = true,
    val isChildSafetyActive: Boolean = true,
    val isLoneOccupantActive: Boolean = false,
    val autoShutoffStoveMinutes: Int = 180, // 3 hours
    val noMotionThresholdHours: Int = 12,
    val emergencyContactPhone: String = "+91 98765 43210"
)

// ─── ENHANCEMENT 3: AI Coach Personality ("IQ") ───
data class ProactiveInsight(
    val id: String,
    val timeOfDay: String,
    val greeting: String,
    val message: String,
    val actionLabel: String? = null,
    val actionType: String? = null
)

// ─── ENHANCEMENT 4: Energy Savings Gamification ───
data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val progress: Float, // 0.0 .. 1.0
    val rewardPoints: Int,
    val isCompleted: Boolean = false
)

data class Streak(
    val days: Int = 7,
    val weeklySavingsRupees: Double = 340.0,
    val nextMilestoneDays: Int = 10
)

data class Achievement(
    val id: String,
    val icon: String,
    val title: String,
    val description: String,
    val isUnlocked: Boolean = true,
    val unlockedDate: String? = "Oct 12, 2026"
)

data class LeaderboardEntry(
    val rank: Int,
    val member: FamilyMember,
    val scorePoints: Int,
    val savingsKwh: Double
)

data class HomeState(
    val rooms: List<Room> = emptyList(),
    val energy: EnergyData = EnergyData(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val security: SecurityStatus = SecurityStatus(),
    val weather: WeatherInfo = WeatherInfo(32.5, "Sunny", 65, 12.0),
    val automations: List<AutomationRule> = emptyList(),
    val channels: List<ChannelCustomization> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val wearable: WearableDevice = WearableDevice(),
    val phoneCompanion: PhoneCompanion = PhoneCompanion(),
    val userProfile: UserProfile = UserProfile(),
    val isProfileOpen: Boolean = false,
    val authMode: String = "PROFILE", // "PROFILE", "LOGIN", "SIGNUP"
    val isLocationPickerOpen: Boolean = false,
    val selectedCityIndex: Int = 0,
    val availableCities: List<CityLocation> = listOf(
        CityLocation("blr", "Bengaluru", "IN", 28.5, "Sunny", "☀️", 65),
        CityLocation("hyd", "Hyderabad", "IN", 31.2, "Clear Sky", "🌤️", 58),
        CityLocation("sjc", "San Jose", "CA", 22.0, "Mild Breeze", "⛅", 45),
        CityLocation("lon", "London", "UK", 16.4, "Light Rain", "🌧️", 78),
        CityLocation("nyc", "New York", "NY", 19.8, "Partly Cloudy", "⛅", 52)
    ),
    val liveDateTimeString: String = "Thu, Oct 15 • 11:15 PM",
    val currentLocation: String = "villa",
    val availableLocations: List<HomeLocation> = listOf(
        HomeLocation("villa", "Main Villa", "🏡"),
        HomeLocation("beach", "Beach House", "🏖️"),
        HomeLocation("loft", "City Loft", "🏙️")
    ),
    val tariff: TariffStatus = TariffStatus(),
    val selectedAnalyticsRange: String = "Week",
    val analyticsDateText: String = "Oct 9 – Oct 15, 2026",
    val analyticsBarData: List<Double> = listOf(2.1, 4.2, 2.5, 3.1, 2.8, 1.9, 1.2),
    val analyticsLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    val aiPromptQuery: String = "",
    val aiInputText: String = "",
    val areLightsAllOff: Boolean = false,
    val isNightModeActive: Boolean = false,
    val isEcoCoolingActive: Boolean = false,
    val searchQuery: String = "",
    val isSearchOpen: Boolean = false,
    val isSettingsOpen: Boolean = false,
    val isCustomizationOpen: Boolean = false,
    val isConnectHubOpen: Boolean = false,
    val connectHubTab: Int = 0,
    val hudNotification: String? = null,
    val activeTheme: String = "MIDNIGHT_NAVY", // MIDNIGHT_NAVY, OLED_BLACK, NEON_CYBER
    val aiInsight: String = "Your AC accounts for 42% of total energy. Setting it to 26°C could save ₹85/month.",
    val wearableBriefing: String = "Bee Pioneer Audio Briefing: Home energy usage is at 2.4 kW. All doors locked.",
    val isAiLoading: Boolean = false,
    val ecoMode: Boolean = false,

    // Enhancement 2: Family Guardian
    val activeAnomaly: Anomaly? = null,
    val anomalyHistory: List<Anomaly> = emptyList(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val familyGuardianSettings: FamilyGuardianSettings = FamilyGuardianSettings(),
    val isFamilyGuardianOpen: Boolean = false,

    // Enhancement 3: AI Coach ("IQ")
    val proactiveInsight: ProactiveInsight? = null,
    val dailyGreeting: String = "Good evening Alex! I'm IQ, your home companion.",

    // Enhancement 4: Gamification
    val challenges: List<Challenge> = emptyList(),
    val ecoStreak: Streak = Streak(7, 340.0, 10),
    val achievements: List<Achievement> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val isGamificationOpen: Boolean = false
)


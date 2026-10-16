package com.homeiq.firetv.services

import com.homeiq.firetv.model.HomeState
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse
import org.json.JSONArray
import org.json.JSONObject

object BedrockHomeService {

    private val client: BedrockRuntimeClient? by lazy {
        val accessKey = System.getenv("AWS_ACCESS_KEY")
        val secretKey = System.getenv("AWS_SECRET_KEY")
        if (!accessKey.isNullOrEmpty() && !secretKey.isNullOrEmpty()) {
            try {
                BedrockRuntimeClient.builder()
                    .region(Region.AP_SOUTH_1)
                    .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                        )
                    )
                    .build()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private const val MODEL = "anthropic.claude-3-haiku-20240307-v1:0"

    // ENHANCEMENT 3: System Prompt with "IQ" Personality
    private const val SYSTEM_PROMPT = """
    You are IQ, the intelligent, warm, emotionally-aware smart home companion for the Mercer family living in Bengaluru.
    You are NOT a boring chatbot; you are a personal family energy coach and safety guardian.
    You address family members by name (Alex, Priya, Grandma Savithri, Aarav).
    You speak conversationally with gentle wit, practical energy wisdom, and deep care for family safety (especially Elder Care & Child Safety).
    Keep responses concise (max 25-30 words), high-impact, and actionable for 10-foot Fire TV reading.
    """

    fun getEnergyInsight(state: HomeState): String {
        val prompt = """
        System: $SYSTEM_PROMPT
        Current User: ${state.userProfile.name}
        Context:
        - Current usage: ${state.energy.currentKw} kW
        - Today: ${state.energy.todayKwh} kWh, Cost: ₹${state.energy.costToday}
        - Devices on: ${state.rooms.flatMap { it.devices }.filter { it.isOn }.map { "${it.name} (${it.powerWatts}W)" }.joinToString(", ")}
        - Weather: ${state.weather.condition}, ${state.weather.temp}°C
        
        Give ONE friendly, personalized energy-saving tip from IQ with your name and personality.
        """.trimIndent()

        return invokeBedrock(prompt) ?: generateFallbackInsight(state)
    }

    fun getSecurityInsight(state: HomeState): String {
        val prompt = """
        System: $SYSTEM_PROMPT
        Security status: Mode=${state.security.mode}, Doors locked=${state.security.doorsLocked}/${state.security.totalDoors}, 
        Cameras active=${state.security.camerasActive}. Last event: ${state.security.lastEvent}.
        Elder Care status: ${if (state.familyGuardianSettings.isElderCareActive) "Active" else "Disabled"}.
        
        Give a brief, reassuring safety summary from IQ (max 20 words).
        """.trimIndent()

        return invokeBedrock(prompt) ?: "Hey Alex, all 8 perimeter beams are armed. Grandma's room temperature is a comfortable 24°C."
    }

    fun getAutomationSuggestion(state: HomeState): String {
        val prompt = """
        System: $SYSTEM_PROMPT
        Home state: ${state.rooms.size} rooms, ${state.rooms.flatMap { it.devices }.count { it.isOn }} active devices.
        Weather: ${state.weather.condition} ${state.weather.temp}°C.
        
        Suggest ONE smart proactive automation rule from IQ (max 25 words).
        """.trimIndent()

        return invokeBedrock(prompt) ?: "Hey Alex! Want me to auto-switch living room AC to 26°C when everyone is asleep? Saves ₹85/mo."
    }

    fun generateProactiveGreeting(state: HomeState, hourOfDay: Int): String {
        val name = state.userProfile.name.split(" ").firstOrNull() ?: "Alex"
        return when (hourOfDay) {
            in 5..11 -> "Good morning $name! ☀️ Solar panels generated 4.2 kWh overnight. Ready to conquer your daily eco goal?"
            in 12..16 -> "Good afternoon $name! ⚡ Peak tariff begins at 5 PM. Want me to precool the living room now?"
            in 17..21 -> "Winding down, $name? 🌙 Switched bedroom lights to circadian amber. Living room AC is at Eco 26°C."
            else -> "Still up, $name? 🌌 Family Guardian is active. Kitchen stove anomaly resolved; sleep soundly."
        }
    }

    private fun generateFallbackInsight(state: HomeState): String {
        val name = state.userProfile.name.split(" ").firstOrNull() ?: "Alex"
        val insights = listOf(
            "Hey $name! I noticed your AC has been running 4 hours. Switching to Eco 26°C will save ₹85 this month.",
            "Good news $name! Your solar generation hit 14.8 kWh today — 15% higher than last Tuesday!",
            "IQ Coach: Peak tariff kicks in at 6 PM. Shall I shed living room standby loads to save ₹120 this week?",
            "Hey $name, Grandma Savithri is resting comfortably in the bedroom. Elder Care sensors report all clear.",
            "Smart Home Tip: Dimming ceiling lights to 60% saves 35% power with identical ambient warmth."
        )
        return insights.random()
    }

    private fun invokeBedrock(prompt: String): String? {
        val bedrockClient = client ?: return null
        return try {
            val body = JSONObject().apply {
                put("anthropic_version", "bedrock-2023-05-31")
                put("max_tokens", 256)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply { put("role", "user"); put("content", prompt) })
                })
            }.toString()

            val request = InvokeModelRequest.builder()
                .modelId(MODEL)
                .body(SdkBytes.fromUtf8String(body))
                .build()

            val response: InvokeModelResponse = bedrockClient.invokeModel(request)
            val responseJson = JSONObject(response.body().asUtf8String())
            responseJson.getJSONArray("content").getJSONObject(0).getString("text").trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

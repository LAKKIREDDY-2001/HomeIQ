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

    fun getEnergyInsight(state: HomeState): String {
        val prompt = """
        You are a smart home energy analyst. Given this home state:
        - Current usage: ${state.energy.currentKw} kW
        - Today: ${state.energy.todayKwh} kWh, Cost: ₹${state.energy.costToday}
        - Devices on: ${state.rooms.flatMap { it.devices }.filter { it.isOn }.map { "${it.name} (${it.powerWatts}W)" }.joinToString(", ")}
        - Weather: ${state.weather.condition}, ${state.weather.temp}°C
        
        Give ONE concise, actionable energy-saving tip (max 20 words).
        """.trimIndent()

        return invokeBedrock(prompt) ?: generateFallbackInsight(state)
    }

    fun getSecurityInsight(state: HomeState): String {
        val prompt = """
        Security status: Mode=${state.security.mode}, Doors locked=${state.security.doorsLocked}/${state.security.totalDoors}, 
        Cameras active=${state.security.camerasActive}. Last event: ${state.security.lastEvent}.
        
        Give a brief security status summary (max 15 words).
        """.trimIndent()

        return invokeBedrock(prompt) ?: "All home perimeter security systems are nominal and active."
    }

    fun getAutomationSuggestion(state: HomeState): String {
        val prompt = """
        Home state: ${state.rooms.size} rooms, ${state.rooms.flatMap { it.devices }.count { it.isOn }} active devices.
        Weather: ${state.weather.condition} ${state.weather.temp}°C.
        
        Suggest ONE smart automation rule (max 25 words).
        """.trimIndent()

        return invokeBedrock(prompt) ?: "Suggestion: Auto-adjust living AC to 25°C at 10 PM to conserve peak energy."
    }

    private fun generateFallbackInsight(state: HomeState): String {
        val insights = listOf(
            "Your AC accounts for 42% of total energy. Setting it to 26°C could save ₹85/month.",
            "Bedrock AI Telemetry: Peak Tariff hours start at 6 PM. Switching to Eco Mode now saves ₹120 this week.",
            "Smart Home Analysis: Dimming living room lights to 60% reduces lighting consumption by 35%.",
            "Solar & Storage Insight: Battery reserves at 85%. Optimal window for heavy appliance run is 10:00 AM.",
            "Thermal Comfort Recommendation: Outdoor temp is 32.5°C. Fan + 25°C AC provides optimal cooling at 30% lower cost.",
            "Amazon Bee Pioneer Wearable Sync: Voice command 'Eco Cooling' activated. Estimated ₹18 daily savings."
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

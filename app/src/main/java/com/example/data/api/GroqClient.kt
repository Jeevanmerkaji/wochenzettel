package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.MoshiProvider
import com.example.data.model.*
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// --- OpenAI-compatible request/response shapes (used by Groq, and by most other free-tier providers) ---

@JsonClass(generateAdapter = true)
data class GroqMessage(val role: String, val content: String)

@JsonClass(generateAdapter = true)
data class GroqResponseFormat(val type: String = "json_object")

@JsonClass(generateAdapter = true)
data class GroqChatRequest(
    val model: String,
    val messages: List<GroqMessage>,
    val temperature: Float? = null,
    val response_format: GroqResponseFormat? = null
)

@JsonClass(generateAdapter = true)
data class GroqChoiceMessage(val message: GroqMessage)

@JsonClass(generateAdapter = true)
data class GroqChatResponse(val choices: List<GroqChoiceMessage>?)

interface GroqApiService {
    @POST("openai/v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: GroqChatRequest
    ): GroqChatResponse
}

/**
 * Free-tier test provider. Groq's API is OpenAI-compatible and offers a generous
 * no-credit-card free tier, which is handy for testing before switching to a paid
 * provider (e.g. Gemini) for a production release.
 *
 * Swap MODEL for any other Groq-hosted model name if needed (see console.groq.com).
 */
object GroqClient {
    private const val TAG = "GroqClient"
    private const val BASE_URL = "https://api.groq.com/"
    private const val MODEL = "llama-3.3-70b-versatile"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(MoshiProvider.moshi))
        .build()

    val service: GroqApiService = retrofit.create(GroqApiService::class.java)

    fun isApiKeyAvailable(): Boolean {
        val key = BuildConfig.GROQ_API_KEY
        return key.isNotEmpty() && key != "MY_GROQ_API_KEY"
    }

    /**
     * Generates a structured WeeklyPlan based on user preferences, using the same
     * prompt contract as GeminiClient so the rest of the app doesn't need to care
     * which provider produced the plan.
     */
    suspend fun generateWeeklyPlan(
        prefs: UserPreferences,
        pantryItems: List<PantryItem>
    ): WeeklyPlan? {
        if (!isApiKeyAvailable()) {
            Log.w(TAG, "Groq API Key is missing or placeholder.")
            return null
        }

        val householdSize = prefs.householdSize
        val weeklyBudget = prefs.weeklyBudgetEUR
        val daysToPlan = prefs.daysToPlan
        val mealTypes = prefs.mealTypesIncluded.joinToString { it.name }
        val cuisinesList = if (prefs.cuisines.isEmpty()) "Mix Everything" else prefs.cuisines.joinToString { it.name }

        val dietaryList = mutableListOf<String>()
        if (prefs.vegetarian) dietaryList.add("Vegetarian (no meat)")
        if (prefs.vegan) dietaryList.add("Vegan (no dairy, no meat, no eggs)")
        if (prefs.dairyFree) dietaryList.add("Dairy-free")
        if (prefs.glutenFree) dietaryList.add("Gluten-free (no flour, breadcrumbs, wheat, pasta unless gluten-free specified)")
        if (prefs.noPork) dietaryList.add("No Pork / Halal")
        val diets = if (dietaryList.isEmpty()) "None" else dietaryList.joinToString()

        val pantryStr = if (pantryItems.isEmpty()) "Empty" else pantryItems.joinToString { "${it.ingredientName} (${it.quantityOwned} ${it.unit})" }
        val supermarketContext = supermarketContextFor(prefs.preferredSupermarket)

        val systemPrompt = """
            You are "Wochenzettel", an elite German AI meal planner assistant.
            You must plan a weekly menu that is realistic for German supermarkets ($supermarketContext) and budget-focused.
            You generate a WeeklyPlan JSON object.

            Strict structural rules:
            1. Scale recipe ingredient quantities and costs assuming 4 base servings scaled by householdSize/4.0.
            2. Estimated prices must be in Euros (€), honest and typical for $supermarketContext.
            3. Do not repeat recipes in the same week.
            4. Only generate the requested mealTypes for each day.
            5. Ensure strict dietary filtering (e.g. vegetarian must not contain Meat; noPork/Halal must have pork=false).
            6. Use German-friendly, down-to-earth names and descriptions. Recipe instructions should be in German.
            7. Output a valid, clean JSON object and nothing else - no markdown, no commentary.

            JSON schema you MUST follow:
            {
              "days": [
                {
                  "dayName": "Montag",
                  "breakfast": {
                    "recipe": {
                      "id": "g_b1",
                      "name": "Recipe Name in German",
                      "emoji": "🥞",
                      "cuisine": "GERMAN",
                      "mealType": "BREAKFAST",
                      "vegetarian": true,
                      "pork": false,
                      "cookTimeMinutes": 15,
                      "skillLevel": 1,
                      "costPerServingEUR": 1.20,
                      "proteinGrams": 12,
                      "kcal": 350,
                      "ingredients": [
                        { "name": "Zutat", "quantity": 100.0, "unit": "g", "aisle": "PRODUCE" }
                      ],
                      "instructions": ["Schritt 1...", "Schritt 2..."]
                    },
                    "scaledIngredients": [
                      { "name": "Zutat", "quantity": 50.0, "unit": "g", "aisle": "PRODUCE" }
                    ],
                    "scaledCostEUR": 2.40
                  },
                  "lunch": { ... },
                  "snack": { ... },
                  "dinner": { ... }
                }
              ],
              "totalCostEUR": 45.50
            }
        """.trimIndent()

        val userPrompt = """
            Please generate a weekly meal plan with the following inputs:
            - Days to plan: $daysToPlan
            - Household size: $householdSize
            - Weekly grocery budget: €$weeklyBudget
            - Preferred supermarket: $supermarketContext
            - Include these meal types: $mealTypes
            - Cuisine preferences: $cuisinesList
            - Surprise me with wildcards: ${prefs.surpriseMe}
            - Dietary restrictions: $diets
            - Max cooking time: ${prefs.maxCookTimeMinutes} minutes
            - Cooking skill level: ${prefs.skillLevel} (1=beginner, 2=intermediate, 3=advanced)
            - Current pantry inventory (to keep in mind, although shopping list will handle subtraction): $pantryStr

            Return ONLY the raw JSON object, following the schema exactly.
        """.trimIndent()

        val request = GroqChatRequest(
            model = MODEL,
            messages = listOf(
                GroqMessage(role = "system", content = systemPrompt),
                GroqMessage(role = "user", content = userPrompt)
            ),
            temperature = 0.7f,
            response_format = GroqResponseFormat(type = "json_object")
        )

        return try {
            val response = service.chatCompletion("Bearer ${BuildConfig.GROQ_API_KEY}", request)
            val jsonText = response.choices?.firstOrNull()?.message?.content
            if (jsonText != null) {
                Log.d(TAG, "Groq Response JSON: $jsonText")
                val adapter = MoshiProvider.moshi.adapter(WeeklyPlan::class.java)
                val cleanJsonText = cleanJsonMarkdown(jsonText)
                val plan = adapter.fromJson(cleanJsonText)
                plan?.copy(id = 1, dateCreated = System.currentTimeMillis())
            } else {
                Log.e(TAG, "Empty content in Groq response.")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating plan with Groq: ", e)
            null
        }
    }

    private fun cleanJsonMarkdown(raw: String): String {
        var clean = raw.trim()
        if (clean.startsWith("```json")) {
            clean = clean.substringAfter("```json").trim()
        } else if (clean.startsWith("```")) {
            clean = clean.substringAfter("```").trim()
        }
        if (clean.endsWith("```")) {
            clean = clean.substringBeforeLast("```").trim()
        }
        return clean
    }
}

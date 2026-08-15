package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.MoshiProvider
import com.example.data.model.*
import com.squareup.moshi.JsonClass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(val text: String? = null)

@JsonClass(generateAdapter = true)
data class Content(val parts: List<Part>)

@JsonClass(generateAdapter = true)
data class ResponseFormat(val mimeType: String)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(val content: Content)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(val candidates: List<Candidate>?)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

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

    val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    fun isApiKeyAvailable(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
    }

    /**
     * Generates a structured WeeklyPlan based on user preferences.
     */
    suspend fun generateWeeklyPlan(
        prefs: UserPreferences,
        pantryItems: List<PantryItem>
    ): WeeklyPlan? {
        if (!isApiKeyAvailable()) {
            Log.w(TAG, "API Key is missing or placeholder.")
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
            7. Output a valid, clean JSON that matches the following structure exactly. Do not wrap in markdown or anything else except plain text.
            
            JSON schema you MUST follow:
            {
              "days": [
                {
                  "dayName": "Monday", // up to ${daysToPlan} days, use German names like "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"
                  "breakfast": { // only include if Breakfast is requested
                    "recipe": {
                      "id": "g_b1", // unique string starting with "g_"
                      "name": "Recipe Name in German",
                      "emoji": "🥞",
                      "cuisine": "GERMAN", // enum value: GERMAN, ITALIAN, INDIAN, TURKISH, MEXICAN, CHINESE, KOREAN, MEDITERRANEAN, MIX
                      "mealType": "BREAKFAST",
                      "vegetarian": true,
                      "pork": false,
                      "cookTimeMinutes": 15,
                      "skillLevel": 1, // 1 to 3
                      "costPerServingEUR": 1.20, // double base cost for 4 servings
                      "proteinGrams": 12,
                      "kcal": 350,
                      "ingredients": [
                        { "name": "Zutat", "quantity": 100.0, "unit": "g", "aisle": "PRODUCE" } // aisle: PRODUCE, DAIRY, MEAT, PANTRY
                      ],
                      "instructions": [
                        "Schritt 1...",
                        "Schritt 2..."
                      ]
                    },
                    "scaledIngredients": [ // scaled by householdSize/4.0
                      { "name": "Zutat", "quantity": 50.0, "unit": "g", "aisle": "PRODUCE" }
                    ],
                    "scaledCostEUR": 2.40 // scaledCost = costPerServingEUR * householdSize
                  },
                  "lunch": { ... ScaledRecipe ... }, // only if requested
                  "snack": { ... ScaledRecipe ... }, // only if requested
                  "dinner": { ... ScaledRecipe ... } // only if requested
                }
              ],
              "totalCostEUR": 45.50 // Sum of scaledCostEUR of all planned meals
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

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = userPrompt)))),
            generationConfig = GenerationConfig(responseMimeType = "application/json", temperature = 0.7f),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = service.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                Log.d(TAG, "Gemini Response JSON: $jsonText")
                val adapter = MoshiProvider.moshi.adapter(WeeklyPlan::class.java)
                val cleanJsonText = cleanJsonMarkdown(jsonText)
                val plan = adapter.fromJson(cleanJsonText)
                plan?.copy(id = 1, dateCreated = System.currentTimeMillis())
            } else {
                Log.e(TAG, "Empty text response from Gemini.")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error generating plan with Gemini: ", e)
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

/** Describes which store(s) the AI should bias product names and price estimates toward. */
internal fun supermarketContextFor(supermarket: Supermarket): String = when (supermarket) {
    Supermarket.MIX -> "REWE, Lidl, Aldi, Edeka"
    Supermarket.LIDL -> "Lidl"
    Supermarket.ALDI -> "Aldi (Nord/Süd)"
    Supermarket.REWE -> "REWE"
    Supermarket.EDEKA -> "Edeka"
    Supermarket.KAUFLAND -> "Kaufland"
    Supermarket.PENNY -> "Penny"
    Supermarket.NETTO -> "Netto Marken-Discount"
}

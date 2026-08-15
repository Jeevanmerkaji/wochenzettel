package com.example.data.repository

import android.util.Log
import com.example.data.api.GeminiClient
import com.example.data.api.GroqClient
import com.example.data.auth.AuthRepository
import com.example.data.local.AppDatabase
import com.example.data.local.LocalRecipeProvider
import com.example.data.model.*
import com.example.data.remote.FirebaseSyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max

class MealPlannerRepository(private val database: AppDatabase) {
    private val TAG = "MealPlannerRepository"

    val userPreferencesFlow: Flow<UserPreferences?> = database.userPreferencesDao().getUserPreferencesFlow()
    val weeklyPlanFlow: Flow<WeeklyPlan?> = database.weeklyPlanDao().getWeeklyPlanFlow()
    val pantryItemsFlow: Flow<List<PantryItem>> = database.pantryDao().getAllPantryItemsFlow()

    suspend fun saveUserPreferences(prefs: UserPreferences) = withContext(Dispatchers.IO) {
        database.userPreferencesDao().insertUserPreferences(prefs)
        AuthRepository.currentUserId?.let { uid -> FirebaseSyncRepository.pushUserPreferences(uid, prefs) }
    }

    suspend fun getUserPreferences(): UserPreferences? = withContext(Dispatchers.IO) {
        database.userPreferencesDao().getUserPreferences()
    }

    suspend fun getWeeklyPlan(): WeeklyPlan? = withContext(Dispatchers.IO) {
        database.weeklyPlanDao().getWeeklyPlan()
    }

    suspend fun clearWeeklyPlan() = withContext(Dispatchers.IO) {
        database.weeklyPlanDao().clearWeeklyPlan()
        AuthRepository.currentUserId?.let { uid -> FirebaseSyncRepository.clearWeeklyPlan(uid) }
    }

    /**
     * Wipes all local Room data. Must be called on sign-out — otherwise the next account to
     * sign in on this device would see the previous account's leftover local preferences/pantry/
     * plan (and syncFromCloud would even mistakenly push that stale data into the new account's
     * Firestore doc, thinking it's "first-time local data" for that user).
     */
    suspend fun clearAllLocalData() = withContext(Dispatchers.IO) {
        database.userPreferencesDao().clearUserPreferences()
        database.pantryDao().clearPantry()
        database.weeklyPlanDao().clearWeeklyPlan()
    }

    /**
     * Called right after a user signs in. If they already have data saved under their account,
     * it becomes the local source of truth (overwriting whatever was in Room before login). If
     * this is their first time signing in on any device, we push whatever's currently local up
     * to Firestore instead, so nothing is lost.
     */
    suspend fun syncFromCloud(uid: String) = withContext(Dispatchers.IO) {
        val cloudPrefs = FirebaseSyncRepository.pullUserPreferences(uid)
        if (cloudPrefs != null) {
            database.userPreferencesDao().insertUserPreferences(cloudPrefs)
        } else {
            getUserPreferences()?.let { FirebaseSyncRepository.pushUserPreferences(uid, it) }
        }

        val cloudPantry = FirebaseSyncRepository.pullPantryItems(uid)
        if (cloudPantry.isNotEmpty()) {
            database.pantryDao().clearPantry()
            cloudPantry.forEach { database.pantryDao().insertPantryItem(it) }
        } else {
            database.pantryDao().getAllPantryItemsFlow().firstOrNull()?.forEach { item ->
                FirebaseSyncRepository.pushPantryItem(uid, item)
            }
        }

        val cloudPlan = FirebaseSyncRepository.pullWeeklyPlan(uid)
        if (cloudPlan != null) {
            database.weeklyPlanDao().insertWeeklyPlan(cloudPlan)
        } else {
            getWeeklyPlan()?.let { FirebaseSyncRepository.pushWeeklyPlan(uid, it) }
        }
    }

    // --- Pantry Management ---

    suspend fun addPantryItem(name: String, quantity: Double, unit: String) = withContext(Dispatchers.IO) {
        val trimmedName = name.trim()
        val trimmedUnit = unit.trim()
        val existing = database.pantryDao().getPantryItemByNameAndUnit(trimmedName, trimmedUnit)
        val saved = if (existing != null) {
            val updated = existing.copy(quantityOwned = existing.quantityOwned + quantity)
            database.pantryDao().insertPantryItem(updated)
            updated
        } else {
            val newItem = PantryItem(ingredientName = trimmedName, unit = trimmedUnit, quantityOwned = quantity)
            database.pantryDao().insertPantryItem(newItem)
            // Room's autoGenerate id is only known after insert; re-read so the Firestore doc ID matches.
            database.pantryDao().getPantryItemByNameAndUnit(trimmedName, trimmedUnit) ?: newItem
        }
        AuthRepository.currentUserId?.let { uid -> FirebaseSyncRepository.pushPantryItem(uid, saved) }
    }

    suspend fun removePantryItem(id: Int) = withContext(Dispatchers.IO) {
        database.pantryDao().deletePantryItemById(id)
        AuthRepository.currentUserId?.let { uid -> FirebaseSyncRepository.deletePantryItem(uid, id) }
    }

    suspend fun clearPantry() = withContext(Dispatchers.IO) {
        val uid = AuthRepository.currentUserId
        val existingIds = if (uid != null) {
            database.pantryDao().getAllPantryItemsFlow().firstOrNull()?.map { it.id } ?: emptyList()
        } else emptyList()
        database.pantryDao().clearPantry()
        uid?.let { FirebaseSyncRepository.clearPantry(it, existingIds) }
    }

    // --- Plan Generation & Optimization Engine ---

    /**
     * Generates a weekly plan. Tries Gemini API first if available,
     * then falls back to a highly robust rule-based local generator.
     */
    suspend fun generateAndSavePlan(
        prefs: UserPreferences,
        onShortfall: (String) -> Unit = {}
    ): Boolean = withContext(Dispatchers.IO) {
        val pantryList = database.pantryDao().getAllPantryItemsFlow().firstOrNull() ?: emptyList()

        val uid = AuthRepository.currentUserId

        // 1. Try Groq first (free tier, handy for testing without a paid key)
        if (GroqClient.isApiKeyAvailable()) {
            val groqPlan = GroqClient.generateWeeklyPlan(prefs, pantryList)
            if (groqPlan != null && groqPlan.days.isNotEmpty()) {
                database.weeklyPlanDao().insertWeeklyPlan(groqPlan)
                uid?.let { FirebaseSyncRepository.pushWeeklyPlan(it, groqPlan) }
                return@withContext true
            }
            Log.w(TAG, "Groq plan generation failed or returned empty; falling back.")
        }

        // 2. Try Gemini (intended for production / release builds)
        if (GeminiClient.isApiKeyAvailable()) {
            val geminiPlan = GeminiClient.generateWeeklyPlan(prefs, pantryList)
            if (geminiPlan != null && geminiPlan.days.isNotEmpty()) {
                database.weeklyPlanDao().insertWeeklyPlan(geminiPlan)
                uid?.let { FirebaseSyncRepository.pushWeeklyPlan(it, geminiPlan) }
                return@withContext true
            }
        }

        // 3. Rule-based local generation fallback
        val plan = generateLocalPlan(prefs, onShortfall)
        if (plan != null) {
            database.weeklyPlanDao().insertWeeklyPlan(plan)
            uid?.let { FirebaseSyncRepository.pushWeeklyPlan(it, plan) }
            return@withContext true
        }
        return@withContext false
    }

    private fun generateLocalPlan(
        prefs: UserPreferences,
        onShortfall: (String) -> Unit
    ): WeeklyPlan? {
        val daysList = listOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag")
        val daysToPlan = prefs.daysToPlan
        val householdSize = prefs.householdSize
        val targetDays = daysList.take(daysToPlan)

        // Generate plans for each selected meal type independently
        val breakfastPlan = generateMealsForType(MealType.BREAKFAST, prefs, daysToPlan, onShortfall)
        val lunchPlan = generateMealsForType(MealType.LUNCH, prefs, daysToPlan, onShortfall)
        val snackPlan = generateMealsForType(MealType.SNACK, prefs, daysToPlan, onShortfall)
        val dinnerPlan = generateMealsForType(MealType.DINNER, prefs, daysToPlan, onShortfall)

        val plannedDays = mutableListOf<PlannedDay>()
        for (i in 0 until daysToPlan) {
            val dayName = targetDays[i]
            plannedDays.add(
                PlannedDay(
                    dayName = dayName,
                    breakfast = breakfastPlan.getOrNull(i),
                    lunch = lunchPlan.getOrNull(i),
                    snack = snackPlan.getOrNull(i),
                    dinner = dinnerPlan.getOrNull(i)
                )
            )
        }

        var totalCost = plannedDays.sumOf { day ->
            (day.breakfast?.scaledCostEUR ?: 0.0) +
            (day.lunch?.scaledCostEUR ?: 0.0) +
            (day.snack?.scaledCostEUR ?: 0.0) +
            (day.dinner?.scaledCostEUR ?: 0.0)
        }

        var swapResult: BudgetSwap? = null

        // --- Budget swap logic (§5.3) ---
        if (totalCost > prefs.weeklyBudgetEUR) {
            Log.d(TAG, "Plan cost ($totalCost) exceeds budget (${prefs.weeklyBudgetEUR}). Running swap logic...")
            
            // Gather all scheduled recipes across the plan to enforce exclusion rules
            val usedRecipeIds = mutableSetOf<String>()
            plannedDays.forEach { day ->
                day.breakfast?.recipe?.id?.let { usedRecipeIds.add(it) }
                day.lunch?.recipe?.id?.let { usedRecipeIds.add(it) }
                day.snack?.recipe?.id?.let { usedRecipeIds.add(it) }
                day.dinner?.recipe?.id?.let { usedRecipeIds.add(it) }
            }

            // Find the single most expensive planned meal
            var mostExpensiveDayIndex = -1
            var mostExpensiveMealType: MealType? = null
            var maxCost = 0.0
            var mostExpensiveRecipe: Recipe? = null

            for (i in 0 until plannedDays.size) {
                val day = plannedDays[i]
                listOf(
                    MealType.BREAKFAST to day.breakfast,
                    MealType.LUNCH to day.lunch,
                    MealType.SNACK to day.snack,
                    MealType.DINNER to day.dinner
                ).forEach { (mType, scaled) ->
                    if (scaled != null && scaled.scaledCostEUR > maxCost) {
                        maxCost = scaled.scaledCostEUR
                        mostExpensiveDayIndex = i
                        mostExpensiveMealType = mType
                        mostExpensiveRecipe = scaled.recipe
                    }
                }
            }

            if (mostExpensiveDayIndex != -1 && mostExpensiveMealType != null && mostExpensiveRecipe != null) {
                val dayName = plannedDays[mostExpensiveDayIndex].dayName
                val expRecipe = mostExpensiveRecipe!!

                // Candidate pool for replacement: cheapear recipe of same type, excluding already used recipes
                val candidatePool = LocalRecipeProvider.recipes.filter { r ->
                    r.mealType == mostExpensiveMealType &&
                    r.id != expRecipe.id &&
                    r.id !in usedRecipeIds &&
                    r.costPerServingEUR < expRecipe.costPerServingEUR &&
                    passesDietaryFilters(r, prefs) &&
                    r.cookTimeMinutes <= prefs.maxCookTimeMinutes
                }.sortedBy { it.costPerServingEUR }

                if (candidatePool.isNotEmpty()) {
                    // Pick the cheapest candidate
                    val replacementRecipe = candidatePool.first()
                    val replacementScaled = scaleRecipe(replacementRecipe, householdSize)
                    
                    // Replace in plan
                    val currentDay = plannedDays[mostExpensiveDayIndex]
                    val updatedDay = when (mostExpensiveMealType) {
                        MealType.BREAKFAST -> currentDay.copy(breakfast = replacementScaled)
                        MealType.LUNCH -> currentDay.copy(lunch = replacementScaled)
                        MealType.SNACK -> currentDay.copy(snack = replacementScaled)
                        MealType.DINNER -> currentDay.copy(dinner = replacementScaled)
                    }
                    plannedDays[mostExpensiveDayIndex] = updatedDay

                    val savings = maxCost - replacementScaled.scaledCostEUR
                    swapResult = BudgetSwap(
                        fromRecipeName = expRecipe.name,
                        toRecipeName = replacementRecipe.name,
                        savingsEUR = savings,
                        dayName = dayName,
                        mealType = mostExpensiveMealType!!
                    )
                    totalCost -= savings
                    Log.d(TAG, "Successfully swapped ${expRecipe.name} with ${replacementRecipe.name} saving €$savings on $dayName")
                }
            }
        }

        return WeeklyPlan(
            id = 1,
            days = plannedDays,
            appliedSwap = swapResult,
            totalCostEUR = totalCost,
            dateCreated = System.currentTimeMillis()
        )
    }

    /**
     * Filters and generates a list of scaled recipes for a specific meal type.
     */
    private fun generateMealsForType(
        mealType: MealType,
        prefs: UserPreferences,
        daysToPlan: Int,
        onShortfall: (String) -> Unit
    ): List<ScaledRecipe> {
        if (mealType !in prefs.mealTypesIncluded) return emptyList()

        // 1. Gather recipe candidates matching basic criteria
        var candidates = LocalRecipeProvider.recipes.filter { r ->
            r.mealType == mealType &&
            r.cookTimeMinutes <= prefs.maxCookTimeMinutes &&
            passesDietaryFilters(r, prefs)
        }

        // 2. Cuisine filtering with "Surprise Me" Wildcards (§5.1)
        val selectedCuisines = prefs.cuisines
        if (selectedCuisines.isNotEmpty()) {
            val mainPool = candidates.filter { r ->
                r.cuisine in selectedCuisines || r.cuisine == Cuisine.MIX
            }
            
            if (prefs.surpriseMe) {
                // Reserves roughly 1-in-3 day-slots for wildcards from other cuisines
                val wildcardPool = candidates.filter { r ->
                    r.cuisine !in selectedCuisines && r.cuisine != Cuisine.MIX
                }.shuffled()

                val resultList = mutableListOf<Recipe>()
                val mainShuffled = mainPool.shuffled().toMutableList()
                val wildcardIterator = wildcardPool.iterator()

                for (dayIndex in 0 until daysToPlan) {
                    val isWildcardDay = (dayIndex % 3 == 2)
                    if (isWildcardDay && wildcardIterator.hasNext()) {
                        resultList.add(wildcardIterator.next())
                    } else if (mainShuffled.isNotEmpty()) {
                        resultList.add(mainShuffled.removeAt(0))
                    } else if (wildcardIterator.hasNext()) {
                        resultList.add(wildcardIterator.next())
                    }
                }
                candidates = resultList
            } else {
                candidates = mainPool
            }
        }

        // 3. Shuffle candidates to make generations feel fresh
        candidates = candidates.shuffled()

        // 4. Never repeat recipes within the same week (§5.1.4)
        val uniqueCandidates = candidates.distinctBy { it.id }
        if (uniqueCandidates.size < daysToPlan) {
            val shortfallCount = daysToPlan - uniqueCandidates.size
            val mealTypeName = when (mealType) {
                MealType.BREAKFAST -> "Frühstück"
                MealType.LUNCH -> "Mittagessen"
                MealType.SNACK -> "Snack"
                MealType.DINNER -> "Abendessen"
            }
            onShortfall("$mealTypeName hatte nur ${uniqueCandidates.size} von $daysToPlan passenden Rezepten.")
        }

        // Take up to daysToPlan
        val chosenRecipes = uniqueCandidates.take(daysToPlan)
        return chosenRecipes.map { scaleRecipe(it, prefs.householdSize) }
    }

    private fun passesDietaryFilters(recipe: Recipe, prefs: UserPreferences): Boolean {
        // Vegetarian: no ingredient in MEAT aisle
        if (prefs.vegetarian) {
            val hasMeat = recipe.ingredients.any { it.aisle == Aisle.MEAT }
            if (hasMeat || !recipe.vegetarian) return false
        }
        
        // Vegan: no MEAT or DAIRY, no egg, milk, butter, or cheese ingredients
        if (prefs.vegan) {
            val hasNonVeganAisle = recipe.ingredients.any { it.aisle == Aisle.MEAT || it.aisle == Aisle.DAIRY }
            if (hasNonVeganAisle) return false
            val nonVeganWords = listOf("ei", "egg", "schinken", "speck", "käse", "sahne", "butter", "milch", "quark")
            val hasNonVeganWord = recipe.ingredients.any { ing ->
                nonVeganWords.any { word -> ing.name.contains(word, ignoreCase = true) }
            }
            if (hasNonVeganWord) return false
        }

        // Dairy-free: no ingredient in DAIRY aisle
        if (prefs.dairyFree) {
            val hasDairy = recipe.ingredients.any { it.aisle == Aisle.DAIRY }
            if (hasDairy) return false
        }

        // No Pork / Halal: pork flag is false
        if (prefs.noPork && recipe.pork) {
            return false
        }

        // Gluten-free: contains no ingredient whose name matches known gluten-bearing words
        if (prefs.glutenFree) {
            val glutenWords = listOf("nudeln", "spaghetti", "penne", "brot", "panade", "schupfnudeln", "mie-nudeln", "teig", "brezel", "laugenstange", "laugenbrötchen", "semmelbrösel", "mie", "biscuit", "mehl")
            val hasGluten = recipe.ingredients.any { ing ->
                glutenWords.any { word -> ing.name.contains(word, ignoreCase = true) }
            }
            if (hasGluten) return false
        }

        return true
    }

    private fun scaleRecipe(recipe: Recipe, householdSize: Int): ScaledRecipe {
        val scaleFactor = householdSize / 4.0
        val scaledIngredients = recipe.ingredients.map { ing ->
            ScaledIngredient(
                name = ing.name,
                quantity = ing.quantity * scaleFactor,
                unit = ing.unit,
                aisle = ing.aisle
            )
        }
        val scaledCost = recipe.costPerServingEUR * householdSize
        return ScaledRecipe(
            recipe = recipe,
            scaledIngredients = scaledIngredients,
            scaledCostEUR = scaledCost
        )
    }
}

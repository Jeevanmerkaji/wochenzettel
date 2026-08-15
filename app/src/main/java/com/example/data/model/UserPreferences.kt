package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "user_preferences")
@JsonClass(generateAdapter = true)
data class UserPreferences(
    @PrimaryKey val id: Int = 1, // Single row constraint
    val householdSize: Int = 2,
    val weeklyBudgetEUR: Double = 100.0,
    val preferredSupermarket: Supermarket = Supermarket.MIX,
    val daysToPlan: Int = 5,
    val mealTypesIncluded: List<MealType> = listOf(MealType.DINNER),
    val cuisines: List<Cuisine> = emptyList(), // empty means "mix everything"
    val surpriseMe: Boolean = false,
    val vegetarian: Boolean = false,
    val vegan: Boolean = false,
    val dairyFree: Boolean = false,
    val glutenFree: Boolean = false,
    val noPork: Boolean = false,
    val maxCookTimeMinutes: Int = 45,
    val skillLevel: Int = 2, // 1=beginner, 2=intermediate, 3=advanced
    val onboardingCompleted: Boolean = false
)

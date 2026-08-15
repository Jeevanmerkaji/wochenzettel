package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ScaledIngredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val aisle: Aisle
)

@JsonClass(generateAdapter = true)
data class ScaledRecipe(
    val recipe: Recipe,
    val scaledIngredients: List<ScaledIngredient>,
    val scaledCostEUR: Double
)

@JsonClass(generateAdapter = true)
data class PlannedDay(
    val dayName: String, // e.g., "Montag", "Dienstag", ... or English equivalent
    val breakfast: ScaledRecipe? = null,
    val lunch: ScaledRecipe? = null,
    val snack: ScaledRecipe? = null,
    val dinner: ScaledRecipe? = null
) {
    fun getMeal(mealType: MealType): ScaledRecipe? {
        return when (mealType) {
            MealType.BREAKFAST -> breakfast
            MealType.LUNCH -> lunch
            MealType.SNACK -> snack
            MealType.DINNER -> dinner
        }
    }
}

@JsonClass(generateAdapter = true)
data class BudgetSwap(
    val fromRecipeName: String,
    val toRecipeName: String,
    val savingsEUR: Double,
    val dayName: String,
    val mealType: MealType
)

@Entity(tableName = "weekly_plan")
@JsonClass(generateAdapter = true)
data class WeeklyPlan(
    @PrimaryKey val id: Int = 1, // Single active weekly plan
    val days: List<PlannedDay> = emptyList(),
    val appliedSwap: BudgetSwap? = null,
    val totalCostEUR: Double = 0.0,
    val dateCreated: Long = System.currentTimeMillis()
)

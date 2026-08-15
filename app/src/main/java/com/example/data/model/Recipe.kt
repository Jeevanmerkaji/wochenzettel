package com.example.data.model

import com.squareup.moshi.JsonClass

enum class Cuisine {
    GERMAN, ITALIAN, INDIAN, TURKISH, MEXICAN, CHINESE, KOREAN, MEDITERRANEAN, MIX;

    companion object {
        fun fromString(value: String): Cuisine {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MIX
        }
    }
}

enum class MealType {
    BREAKFAST, LUNCH, SNACK, DINNER;

    companion object {
        fun fromString(value: String): MealType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: DINNER
        }
    }
}

enum class Aisle {
    PRODUCE, DAIRY, MEAT, PANTRY;

    companion object {
        fun fromString(value: String): Aisle {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PANTRY
        }
    }
}

enum class Supermarket {
    MIX, LIDL, ALDI, REWE, EDEKA, KAUFLAND, PENNY, NETTO;

    companion object {
        fun fromString(value: String): Supermarket {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MIX
        }
    }
}

@JsonClass(generateAdapter = true)
data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: String, // g, kg, ml, l, tbsp, tsp, pcs, cloves, cans, bunch, knob, etc.
    val aisle: Aisle
)

@JsonClass(generateAdapter = true)
data class Recipe(
    val id: String,
    val name: String,
    val emoji: String,
    val cuisine: Cuisine,
    val mealType: MealType,
    val vegetarian: Boolean,
    val pork: Boolean, // true if contains pork, used for halal/no-pork filtering
    val cookTimeMinutes: Int,
    val skillLevel: Int, // 1=beginner, 2=intermediate, 3=advanced
    val costPerServingEUR: Double, // base cost assuming 4 servings
    val proteinGrams: Int,
    val kcal: Int,
    val ingredients: List<Ingredient>,
    val instructions: List<String>
)

package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiProvider {
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
}

class AppConverters {
    private val moshi = MoshiProvider.moshi

    // --- PlannedDay List ---
    @TypeConverter
    fun fromPlannedDayList(list: List<PlannedDay>?): String {
        if (list == null) return "[]"
        val type = Types.newParameterizedType(List::class.java, PlannedDay::class.java)
        val adapter = moshi.adapter<List<PlannedDay>>(type)
        return adapter.toJson(list)
    }

    @TypeConverter
    fun toPlannedDayList(json: String?): List<PlannedDay> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = Types.newParameterizedType(List::class.java, PlannedDay::class.java)
        val adapter = moshi.adapter<List<PlannedDay>>(type)
        return adapter.fromJson(json) ?: emptyList()
    }

    // --- BudgetSwap ---
    @TypeConverter
    fun fromBudgetSwap(swap: BudgetSwap?): String? {
        if (swap == null) return null
        val adapter = moshi.adapter(BudgetSwap::class.java)
        return adapter.toJson(swap)
    }

    @TypeConverter
    fun toBudgetSwap(json: String?): BudgetSwap? {
        if (json.isNullOrEmpty()) return null
        val adapter = moshi.adapter(BudgetSwap::class.java)
        return adapter.fromJson(json)
    }

    // --- MealType List ---
    @TypeConverter
    fun fromMealTypeList(list: List<MealType>?): String {
        if (list == null) return ""
        return list.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toMealTypeList(data: String?): List<MealType> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(",").map { MealType.valueOf(it) }
    }

    // --- Cuisine List ---
    @TypeConverter
    fun fromCuisineList(list: List<Cuisine>?): String {
        if (list == null) return ""
        return list.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toCuisineList(data: String?): List<Cuisine> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(",").map { Cuisine.valueOf(it) }
    }

    // --- Supermarket ---
    @TypeConverter
    fun fromSupermarket(value: Supermarket?): String = (value ?: Supermarket.MIX).name

    @TypeConverter
    fun toSupermarket(value: String?): Supermarket {
        if (value.isNullOrEmpty()) return Supermarket.MIX
        return Supermarket.fromString(value)
    }
}

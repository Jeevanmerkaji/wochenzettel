package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry")
data class PantryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ingredientName: String,
    val unit: String,
    val quantityOwned: Double
)

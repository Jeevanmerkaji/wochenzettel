package com.example.data.local

import android.content.Context
import androidx.room.*
import com.example.data.model.PantryItem
import com.example.data.model.UserPreferences
import com.example.data.model.WeeklyPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getUserPreferencesFlow(): Flow<UserPreferences?>

    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    suspend fun getUserPreferences(): UserPreferences?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserPreferences(prefs: UserPreferences)

    @Query("DELETE FROM user_preferences")
    suspend fun clearUserPreferences()
}

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry ORDER BY ingredientName ASC")
    fun getAllPantryItemsFlow(): Flow<List<PantryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItem(item: PantryItem)

    @Query("SELECT * FROM pantry WHERE ingredientName = :name AND unit = :unit LIMIT 1")
    suspend fun getPantryItemByNameAndUnit(name: String, unit: String): PantryItem?

    @Delete
    suspend fun deletePantryItem(item: PantryItem)

    @Query("DELETE FROM pantry WHERE id = :id")
    suspend fun deletePantryItemById(id: Int)

    @Query("DELETE FROM pantry")
    suspend fun clearPantry()
}

@Dao
interface WeeklyPlanDao {
    @Query("SELECT * FROM weekly_plan WHERE id = 1 LIMIT 1")
    fun getWeeklyPlanFlow(): Flow<WeeklyPlan?>

    @Query("SELECT * FROM weekly_plan WHERE id = 1 LIMIT 1")
    suspend fun getWeeklyPlan(): WeeklyPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyPlan(plan: WeeklyPlan)

    @Query("DELETE FROM weekly_plan WHERE id = 1")
    suspend fun clearWeeklyPlan()
}

@Database(
    entities = [UserPreferences::class, PantryItem::class, WeeklyPlan::class],
    version = 2, // bumped: added UserPreferences.preferredSupermarket
    exportSchema = false
)
@TypeConverters(AppConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun pantryDao(): PantryDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wochenzettel_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

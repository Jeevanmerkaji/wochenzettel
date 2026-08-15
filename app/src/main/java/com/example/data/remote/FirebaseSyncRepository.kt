package com.example.data.remote

import android.util.Log
import com.example.data.model.PantryItem
import com.example.data.model.UserPreferences
import com.example.data.model.WeeklyPlan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Mirrors local Room data to Cloud Firestore, keyed by Firebase Auth uid, so a signed-in user's
 * preferences/pantry/weekly plan follow them across devices.
 *
 * Document layout:
 *   users/{uid}                      -> UserPreferences fields
 *   users/{uid}/pantry/{itemId}      -> one PantryItem per doc
 *   users/{uid}/weeklyPlan/current   -> single WeeklyPlan doc
 *
 * Every function fails soft (returns null / empty / silently logs) if Firestore isn't
 * configured or a call fails, so callers never need to special-case "no backend" themselves.
 */
object FirebaseSyncRepository {
    private const val TAG = "FirebaseSyncRepository"
    private const val USERS = "users"
    private const val PANTRY = "pantry"
    private const val WEEKLY_PLAN = "weeklyPlan"
    private const val CURRENT_PLAN_DOC = "current"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore is not available: ${e.message}")
            null
        }
    }

    // --- User Preferences ---

    suspend fun pullUserPreferences(uid: String): UserPreferences? = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.get()?.awaitResult()
                ?.toObject(UserPreferences::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to pull preferences", e)
            null
        }
    }

    suspend fun pushUserPreferences(uid: String, prefs: UserPreferences) = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.set(prefs)?.awaitResult()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to push preferences", e)
        }
    }

    // --- Pantry ---

    suspend fun pullPantryItems(uid: String): List<PantryItem> = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.collection(PANTRY)?.get()?.awaitResult()
                ?.documents?.mapNotNull { it.toObject(PantryItem::class.java) } ?: emptyList()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to pull pantry items", e)
            emptyList()
        }
    }

    suspend fun pushPantryItem(uid: String, item: PantryItem) = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.collection(PANTRY)
                ?.document(item.id.toString())?.set(item)?.awaitResult()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to push pantry item", e)
        }
    }

    suspend fun deletePantryItem(uid: String, itemId: Int) = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.collection(PANTRY)
                ?.document(itemId.toString())?.delete()?.awaitResult()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete pantry item", e)
        }
    }

    suspend fun clearPantry(uid: String, itemIds: List<Int>) = withContext(Dispatchers.IO) {
        itemIds.forEach { deletePantryItem(uid, it) }
    }

    // --- Weekly Plan ---

    suspend fun pullWeeklyPlan(uid: String): WeeklyPlan? = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.collection(WEEKLY_PLAN)
                ?.document(CURRENT_PLAN_DOC)?.get()?.awaitResult()
                ?.toObject(WeeklyPlan::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to pull weekly plan", e)
            null
        }
    }

    suspend fun pushWeeklyPlan(uid: String, plan: WeeklyPlan) = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.collection(WEEKLY_PLAN)
                ?.document(CURRENT_PLAN_DOC)?.set(plan)?.awaitResult()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to push weekly plan", e)
        }
    }

    suspend fun clearWeeklyPlan(uid: String) = withContext(Dispatchers.IO) {
        try {
            firestore?.collection(USERS)?.document(uid)?.collection(WEEKLY_PLAN)
                ?.document(CURRENT_PLAN_DOC)?.delete()?.awaitResult()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to clear weekly plan", e)
        }
    }
}

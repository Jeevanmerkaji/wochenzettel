package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.PantryItem
import com.example.data.model.UserPreferences
import com.example.data.model.WeeklyPlan
import com.example.data.repository.MealPlannerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MealPlannerViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = MealPlannerRepository(database)

    // Tracks whether the initial DB read has completed at least once, so the UI can
    // distinguish "still loading" from "loaded, but no preferences saved yet" — both of
    // which look like `userPreferences.value == null` otherwise.
    private val _isPreferencesLoaded = MutableStateFlow(false)
    val isPreferencesLoaded: StateFlow<Boolean> = _isPreferencesLoaded.asStateFlow()

    val userPreferences: StateFlow<UserPreferences?> = repository.userPreferencesFlow
        .onEach { _isPreferencesLoaded.value = true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val weeklyPlan: StateFlow<WeeklyPlan?> = repository.weeklyPlanFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val pantryItems: StateFlow<List<PantryItem>> = repository.pantryItemsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _shortfallWarning = MutableStateFlow<String?>(null)
    val shortfallWarning: StateFlow<String?> = _shortfallWarning.asStateFlow()

    private val _generationSuccess = MutableStateFlow<Boolean?>(null)
    val generationSuccess: StateFlow<Boolean?> = _generationSuccess.asStateFlow()

    fun clearShortfallWarning() {
        _shortfallWarning.value = null
    }

    fun clearGenerationStatus() {
        _generationSuccess.value = null
    }

    /**
     * Saves onboarding and automatically triggers initial plan generation.
     */
    fun saveOnboarding(prefs: UserPreferences) {
        viewModelScope.launch {
            _isGenerating.value = true
            _shortfallWarning.value = null
            _generationSuccess.value = null
            
            // 1. Save preferences
            val updatedPrefs = prefs.copy(onboardingCompleted = true)
            repository.saveUserPreferences(updatedPrefs)

            // 2. Generate plan
            val success = repository.generateAndSavePlan(updatedPrefs) { warning ->
                _shortfallWarning.value = warning
            }
            _generationSuccess.value = success
            _isGenerating.value = false
        }
    }

    /**
     * Triggers active weekly plan regeneration.
     */
    fun regeneratePlan() {
        viewModelScope.launch {
            val prefs = userPreferences.value ?: return@launch
            _isGenerating.value = true
            _shortfallWarning.value = null
            _generationSuccess.value = null

            val success = repository.generateAndSavePlan(prefs) { warning ->
                _shortfallWarning.value = warning
            }
            _generationSuccess.value = success
            _isGenerating.value = false
        }
    }

    /**
     * Pulls (or, on a brand new account, pushes) this user's cloud-synced data right after
     * they sign in, so local Room state reflects whatever's stored under their account.
     */
    fun onUserSignedIn(uid: String) {
        viewModelScope.launch {
            repository.syncFromCloud(uid)
        }
    }

    /**
     * Must be called on sign-out so the next account to log in on this device doesn't inherit
     * the previous account's local preferences/pantry/plan.
     */
    fun clearLocalDataOnSignOut() {
        viewModelScope.launch {
            repository.clearAllLocalData()
        }
    }

    /**
     * Updates user preferences without re-generating plan.
     */
    fun updatePreferences(prefs: UserPreferences) {
        viewModelScope.launch {
            repository.saveUserPreferences(prefs)
        }
    }

    /**
     * Resets onboarding, letting the user go through the setup flow again.
     */
    fun resetOnboarding() {
        viewModelScope.launch {
            val current = userPreferences.value ?: UserPreferences()
            repository.saveUserPreferences(current.copy(onboardingCompleted = false))
            repository.clearWeeklyPlan()
        }
    }

    // --- Pantry Operations ---

    fun addPantryItem(name: String, quantity: Double, unit: String) {
        viewModelScope.launch {
            repository.addPantryItem(name, quantity, unit)
        }
    }

    fun deletePantryItem(id: Int) {
        viewModelScope.launch {
            repository.removePantryItem(id)
        }
    }

    fun clearPantry() {
        viewModelScope.launch {
            repository.clearPantry()
        }
    }
}

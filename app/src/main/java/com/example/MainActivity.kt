package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.WochenzettelTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.MealPlannerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MealPlannerViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WochenzettelTheme {
                val context = LocalContext.current

                val currentUserState = authViewModel.currentUser.collectAsStateWithLifecycle()
                val isAuthenticatingState = authViewModel.isAuthenticating.collectAsStateWithLifecycle()
                val authErrorState = authViewModel.authError.collectAsStateWithLifecycle()
                val currentUser = currentUserState.value

                val userPrefsState = viewModel.userPreferences.collectAsStateWithLifecycle()
                val isPreferencesLoadedState = viewModel.isPreferencesLoaded.collectAsStateWithLifecycle()
                val weeklyPlanState = viewModel.weeklyPlan.collectAsStateWithLifecycle()
                val pantryItemsState = viewModel.pantryItems.collectAsStateWithLifecycle()
                val isGeneratingState = viewModel.isGenerating.collectAsStateWithLifecycle()
                val shortfallWarningState = viewModel.shortfallWarning.collectAsStateWithLifecycle()
                val generationSuccessState = viewModel.generationSuccess.collectAsStateWithLifecycle()

                val userPrefs = userPrefsState.value
                val isPreferencesLoaded = isPreferencesLoadedState.value
                val weeklyPlan = weeklyPlanState.value
                val pantryItems = pantryItemsState.value
                val isGenerating = isGeneratingState.value
                val shortfallWarning = shortfallWarningState.value
                val generationSuccess = generationSuccessState.value

                // Display local generation warnings as Toast notifications
                LaunchedEffect(shortfallWarning) {
                    shortfallWarning?.let { warning ->
                        Toast.makeText(context, "Hinweis: $warning", Toast.LENGTH_LONG).show()
                        viewModel.clearShortfallWarning()
                    }
                }

                // Whenever a user signs in, pull/push their cloud data once so local Room state
                // reflects whatever is stored under their account.
                LaunchedEffect(currentUser?.uid) {
                    currentUser?.uid?.let { uid -> viewModel.onUserSignedIn(uid) }
                }

                // Handle initial setup logic
                if (authViewModel.isFirebaseConfigured && currentUser == null) {
                    // Signed out (and a backend is actually configured) — require login first.
                    LoginScreen(
                        isFirebaseConfigured = authViewModel.isFirebaseConfigured,
                        isLoading = isAuthenticatingState.value,
                        errorMessage = authErrorState.value,
                        onSignIn = { email, password -> authViewModel.signIn(email, password) },
                        onSignUp = { email, password -> authViewModel.signUp(email, password) },
                        onGoogleSignIn = { authViewModel.signInWithGoogle(context) },
                        onDismissError = { authViewModel.clearError() },
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!isPreferencesLoaded) {
                    // Still waiting on the first DB read to come back.
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (userPrefs == null || !userPrefs.onboardingCompleted) {
                    // Onboarding flow
                    OnboardingScreen(
                        onComplete = { prefs ->
                            viewModel.saveOnboarding(prefs)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Main tab navigation layout
                    var currentTab by remember { mutableIntStateOf(0) }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.testTag("main_navigation_bar"),
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                NavigationBarItem(
                                    selected = currentTab == 0,
                                    onClick = { currentTab = 0 },
                                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Plan") },
                                    label = { Text("Plan") },
                                    modifier = Modifier.testTag("nav_plan")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 1,
                                    onClick = { currentTab = 1 },
                                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Liste") },
                                    label = { Text("Liste") },
                                    modifier = Modifier.testTag("nav_list")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 2,
                                    onClick = { currentTab = 2 },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Vorrat") },
                                    label = { Text("Vorrat") },
                                    modifier = Modifier.testTag("nav_pantry")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 3,
                                    onClick = { currentTab = 3 },
                                    icon = { Icon(Icons.Default.Star, contentDescription = "Budget") },
                                    label = { Text("Budget") },
                                    modifier = Modifier.testTag("nav_budget")
                                )
                                NavigationBarItem(
                                    selected = currentTab == 4,
                                    onClick = { currentTab = 4 },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Profil") },
                                    label = { Text("Einstellungen") },
                                    modifier = Modifier.testTag("nav_profile")
                                )
                            }
                        }
                    ) { innerPadding ->
                        val modifierWithPadding = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)

                        when (currentTab) {
                            0 -> PlanScreen(
                                weeklyPlan = weeklyPlan,
                                householdSize = userPrefs?.householdSize ?: 2,
                                isGenerating = isGenerating,
                                onRegenerate = { viewModel.regeneratePlan() },
                                modifier = modifierWithPadding
                            )
                            1 -> ListScreen(
                                weeklyPlan = weeklyPlan,
                                weeklyBudget = userPrefs?.weeklyBudgetEUR ?: 100.0,
                                pantryItems = pantryItems,
                                modifier = modifierWithPadding
                            )
                            2 -> PantryScreen(
                                pantryItems = pantryItems,
                                onAddItem = { name, qty, unit ->
                                    viewModel.addPantryItem(name, qty, unit)
                                },
                                onDeleteItem = { id ->
                                    viewModel.deletePantryItem(id)
                                },
                                onClearAll = {
                                    viewModel.clearPantry()
                                },
                                modifier = modifierWithPadding
                            )
                            3 -> BudgetScreen(
                                weeklyPlan = weeklyPlan,
                                weeklyBudget = userPrefs?.weeklyBudgetEUR ?: 100.0,
                                modifier = modifierWithPadding
                            )
                            4 -> ProfileScreen(
                                currentPrefs = userPrefs,
                                currentUserEmail = currentUser?.email,
                                onSavePreferences = { updatedPrefs ->
                                    viewModel.updatePreferences(updatedPrefs)
                                    Toast.makeText(context, "Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
                                },
                                onResetOnboarding = {
                                    viewModel.resetOnboarding()
                                    currentTab = 0
                                },
                                onSignOut = {
                                    authViewModel.signOut()
                                    viewModel.clearLocalDataOnSignOut()
                                },
                                modifier = modifierWithPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    currentPrefs: UserPreferences?,
    currentUserEmail: String? = null,
    onSavePreferences: (UserPreferences) -> Unit,
    onResetOnboarding: () -> Unit,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Preferences Edit state (derived from currentPrefs or defaults)
    var householdSize by remember { mutableIntStateOf(currentPrefs?.householdSize ?: 2) }
    var weeklyBudgetInput by remember { mutableStateOf(currentPrefs?.weeklyBudgetEUR?.toString() ?: "100") }
    var preferredSupermarket by remember { mutableStateOf(currentPrefs?.preferredSupermarket ?: Supermarket.MIX) }
    var daysToPlan by remember { mutableIntStateOf(currentPrefs?.daysToPlan ?: 5) }
    
    var mealTypesIncluded by remember { mutableStateOf(currentPrefs?.mealTypesIncluded?.toSet() ?: setOf(MealType.DINNER)) }
    var cuisinesSelected by remember { mutableStateOf(currentPrefs?.cuisines?.toSet() ?: emptySet()) }
    var surpriseMe by remember { mutableStateOf(currentPrefs?.surpriseMe ?: false) }

    var vegetarian by remember { mutableStateOf(currentPrefs?.vegetarian ?: false) }
    var vegan by remember { mutableStateOf(currentPrefs?.vegan ?: false) }
    var dairyFree by remember { mutableStateOf(currentPrefs?.dairyFree ?: false) }
    var glutenFree by remember { mutableStateOf(currentPrefs?.glutenFree ?: false) }
    var noPork by remember { mutableStateOf(currentPrefs?.noPork ?: false) }

    var maxCookTime by remember { mutableIntStateOf(currentPrefs?.maxCookTimeMinutes ?: 45) }
    var skillLevel by remember { mutableIntStateOf(currentPrefs?.skillLevel ?: 2) }

    var showResetDialog by remember { mutableStateOf(false) }

    // Keep fields in sync if currentPrefs changes
    LaunchedEffect(currentPrefs) {
        if (currentPrefs != null) {
            householdSize = currentPrefs.householdSize
            weeklyBudgetInput = currentPrefs.weeklyBudgetEUR.toString()
            preferredSupermarket = currentPrefs.preferredSupermarket
            daysToPlan = currentPrefs.daysToPlan
            mealTypesIncluded = currentPrefs.mealTypesIncluded.toSet()
            cuisinesSelected = currentPrefs.cuisines.toSet()
            surpriseMe = currentPrefs.surpriseMe
            vegetarian = currentPrefs.vegetarian
            vegan = currentPrefs.vegan
            dairyFree = currentPrefs.dairyFree
            glutenFree = currentPrefs.glutenFree
            noPork = currentPrefs.noPork
            maxCookTime = currentPrefs.maxCookTimeMinutes
            skillLevel = currentPrefs.skillLevel
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "EINSTELLUNGEN",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = InkPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 0. Account Card (only shown once cloud sync is actually in use)
            if (currentUserEmail != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Angemeldet als", style = MaterialTheme.typography.labelLarge, color = InkMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(currentUserEmail, style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onSignOut,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrickRed),
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Abmelden")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Abmelden", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 1. Basic Stats & Household Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "HAUSHALT & BUDGET",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkMuted,
                        fontWeight = FontWeight.Bold
                    )

                    // Household Size Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Haushaltsgröße", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                            Text("Anzahl der versorgten Personen", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledIconButton(
                                onClick = { if (householdSize > 1) householdSize-- },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Text(
                                text = householdSize.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary,
                                modifier = Modifier.width(20.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            FilledIconButton(
                                onClick = { if (householdSize < 10) householdSize++ },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }

                    HorizontalDivider(color = HairlineColor.copy(alpha = 0.5f))

                    // Weekly Budget Text Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("Wöchentliches Budget (€)", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                            Text("Lebensmittelausgaben begrenzen", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                        }

                        OutlinedTextField(
                            value = weeklyBudgetInput,
                            onValueChange = { weeklyBudgetInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_budget_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }

                    HorizontalDivider(color = HairlineColor.copy(alpha = 0.5f))

                    // Days to plan Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Tage im Plan", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                            Text("Wie viele Tage im Voraus planen", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledIconButton(
                                onClick = { if (daysToPlan > 1) daysToPlan -= 2 },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Text(
                                text = daysToPlan.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary,
                                modifier = Modifier.width(20.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            FilledIconButton(
                                onClick = { if (daysToPlan < 7) daysToPlan += 2 },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    HorizontalDivider(color = HairlineColor.copy(alpha = 0.5f))

                    // Preferred Supermarket
                    Text("Bevorzugter Supermarkt", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                    Text("Rezepte & Preisschätzungen richten sich danach aus", style = MaterialTheme.typography.bodyMedium, color = InkMuted)

                    val supermarkets = listOf(
                        Supermarket.MIX to "🛒 Gemischt",
                        Supermarket.LIDL to "Lidl",
                        Supermarket.ALDI to "Aldi",
                        Supermarket.REWE to "REWE",
                        Supermarket.EDEKA to "Edeka",
                        Supermarket.KAUFLAND to "Kaufland",
                        Supermarket.PENNY to "Penny",
                        Supermarket.NETTO to "Netto"
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        supermarkets.forEach { (market, label) ->
                            val isChecked = preferredSupermarket == market
                            FilterChip(
                                selected = isChecked,
                                onClick = { preferredSupermarket = market },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // 2. Meal Types & Diet Settings Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "DIÄTEN & MAHLZEITEN",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkMuted,
                        fontWeight = FontWeight.Bold
                    )

                    // Meal Types Included Checkboxes
                    Text("Geplante Mahlzeitentypen", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                    
                    val meals = listOf(
                        MealType.BREAKFAST to "Frühstück",
                        MealType.LUNCH to "Mittagessen",
                        MealType.SNACK to "Snack",
                        MealType.DINNER to "Abendessen"
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        meals.forEach { (type, label) ->
                            val isChecked = mealTypesIncluded.contains(type)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    val next = if (isChecked) mealTypesIncluded - type else mealTypesIncluded + type
                                    // Ensure at least one type remains checked
                                    if (next.isNotEmpty()) mealTypesIncluded = next
                                },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = HairlineColor.copy(alpha = 0.5f))

                    // Dietary switches
                    Text("Diätvorgaben", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)

                    val diets = listOf(
                        Triple("Vegetarisch", vegetarian, { v: Boolean -> vegetarian = v }),
                        Triple("Vegan", vegan, { v: Boolean -> vegan = v }),
                        Triple("Laktosefrei", dairyFree, { v: Boolean -> dairyFree = v }),
                        Triple("Glutenfrei (Best Effort)", glutenFree, { v: Boolean -> glutenFree = v }),
                        Triple("Kein Schweinefleisch", noPork, { v: Boolean -> noPork = v })
                    )

                    diets.forEach { (label, value, onValueChange) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, style = MaterialTheme.typography.bodyLarge, color = InkPrimary)
                            Switch(
                                checked = value,
                                onCheckedChange = onValueChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }

            // 3. Culinary & Preferences Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "KULINARISCHE PRÄFERENZEN",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkMuted,
                        fontWeight = FontWeight.Bold
                    )

                    // Cuisine Chips Selection
                    Text("Küchenrichtungen", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                    
                    val cuisines = listOf(
                        Cuisine.GERMAN to "🇩🇪 Deutsch",
                        Cuisine.ITALIAN to "🇮🇹 Italienisch",
                        Cuisine.MEDITERRANEAN to "🇺🇳 Mediterran",
                        Cuisine.TURKISH to "🇹🇷 Türkisch",
                        Cuisine.INDIAN to "🇮🇳 Indisch",
                        Cuisine.MEXICAN to "🇲🇽 Mexikanisch",
                        Cuisine.CHINESE to "🇨🇳 Chinesisch",
                        Cuisine.KOREAN to "🇰🇷 Koreanisch"
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        cuisines.forEach { (cuisine, label) ->
                            val isChecked = cuisinesSelected.contains(cuisine)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    cuisinesSelected = if (isChecked) cuisinesSelected - cuisine else cuisinesSelected + cuisine
                                },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    if (cuisinesSelected.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Überraschungen erlauben", style = MaterialTheme.typography.bodyLarge, color = InkPrimary, fontWeight = FontWeight.Bold)
                                Text("Streut gelegentlich andere Küchen ein", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                            }
                            Switch(
                                checked = surpriseMe,
                                onCheckedChange = { surpriseMe = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MustardGold,
                                    checkedTrackColor = MustardGold.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }

                    HorizontalDivider(color = HairlineColor.copy(alpha = 0.5f))

                    // Max Cook Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Max. Kochzeit", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                            Text("Kochzeitlimit pro Gericht", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledIconButton(
                                onClick = { if (maxCookTime > 15) maxCookTime -= 15 },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Text(
                                text = "$maxCookTime Min",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary,
                                modifier = Modifier.width(60.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            FilledIconButton(
                                onClick = { if (maxCookTime < 120) maxCookTime += 15 },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    HorizontalDivider(color = HairlineColor.copy(alpha = 0.5f))

                    // Cooking Skill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Kochexpansionsgrad", style = MaterialTheme.typography.titleMedium, color = InkPrimary, fontWeight = FontWeight.Bold)
                            Text("Beginner (1), Intermed (2), Profi (3)", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledIconButton(
                                onClick = { if (skillLevel > 1) skillLevel-- },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Text(
                                text = skillLevel.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary,
                                modifier = Modifier.width(20.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            FilledIconButton(
                                onClick = { if (skillLevel < 3) skillLevel++ },
                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = PaperBg, contentColor = InkPrimary)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // 4. Save Changes Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    val budgetValue = weeklyBudgetInput.toDoubleOrNull() ?: 100.0
                    onSavePreferences(
                        UserPreferences(
                            id = currentPrefs?.id ?: 1,
                            householdSize = householdSize,
                            weeklyBudgetEUR = budgetValue,
                            preferredSupermarket = preferredSupermarket,
                            daysToPlan = daysToPlan,
                            mealTypesIncluded = mealTypesIncluded.toList(),
                            cuisines = cuisinesSelected.toList(),
                            surpriseMe = surpriseMe,
                            vegetarian = vegetarian,
                            vegan = vegan,
                            dairyFree = dairyFree,
                            glutenFree = glutenFree,
                            noPork = noPork,
                            maxCookTimeMinutes = maxCookTime,
                            skillLevel = skillLevel,
                            onboardingCompleted = true
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_preferences_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Einstellungen speichern")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Einstellungen speichern", fontWeight = FontWeight.Bold)
            }

            // 5. Hard Reset Onboarding Button
            OutlinedButton(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrickRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("onboarding_reset_button")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = "Onboarding zurücksetzen")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Onboarding erneut starten", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Onboarding zurücksetzen?", fontWeight = FontWeight.Bold, color = InkPrimary) },
            text = { Text("Dadurch werden alle deine gespeicherten Präferenzen zurückgesetzt und der aktuelle Wochenplan gelöscht. Möchtest du fortfahren?", color = InkPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetOnboarding()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrickRed)
                ) {
                    Text("Ja, zurücksetzen", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Abbrechen", color = InkPrimary)
                }
            },
            containerColor = CardBg
        )
    }
}

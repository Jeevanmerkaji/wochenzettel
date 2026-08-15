package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cuisine
import com.example.data.model.MealType
import com.example.data.model.Supermarket
import com.example.data.model.UserPreferences
import com.example.ui.theme.InkMuted
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.MustardGold
import com.example.ui.theme.PaperBg

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (UserPreferences) -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }
    val totalSteps = 9

    // Onboarding form state
    var householdSize by remember { mutableIntStateOf(2) }
    var budgetOption by remember { mutableStateOf("100") } // "50", "75", "100", "150", "custom"
    var customBudget by remember { mutableStateOf("") }
    var preferredSupermarket by remember { mutableStateOf(Supermarket.MIX) }
    var daysToPlan by remember { mutableIntStateOf(5) }
    // CRITICAL: default to nothing pre-selected as per §6.1
    var mealTypesIncluded by remember { mutableStateOf(emptySet<MealType>()) }
    var cuisinesSelected by remember { mutableStateOf(emptySet<Cuisine>()) }
    var surpriseMe by remember { mutableStateOf(false) }
    
    // Dietary preferences
    var vegetarian by remember { mutableStateOf(false) }
    var vegan by remember { mutableStateOf(false) }
    var dairyFree by remember { mutableStateOf(false) }
    var glutenFree by remember { mutableStateOf(false) }
    var noPork by remember { mutableStateOf(false) }

    var maxCookTime by remember { mutableIntStateOf(45) }
    var skillLevel by remember { mutableIntStateOf(2) } // 1=Beginner, 2=Intermediate, 3=Advanced

    val focusManager = LocalFocusManager.current

    val budgetValue = if (budgetOption == "custom") {
        customBudget.toDoubleOrNull() ?: 100.0
    } else {
        budgetOption.toDoubleOrNull() ?: 100.0
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "WOCHENZETTEL",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = InkPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Schritt $step von $totalSteps",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            // 2. Active Step Content Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    1 -> StepHouseholdSize(householdSize) { householdSize = it }
                    2 -> StepWeeklyBudget(
                        budgetOption = budgetOption,
                        customBudget = customBudget,
                        onOptionSelected = { budgetOption = it },
                        onCustomValueChange = { customBudget = it }
                    )
                    3 -> StepSupermarket(preferredSupermarket) { preferredSupermarket = it }
                    4 -> StepDaysToPlan(daysToPlan) { daysToPlan = it }
                    5 -> StepMealTypes(mealTypesIncluded) { mealTypesIncluded = it }
                    6 -> StepCuisines(
                        selected = cuisinesSelected,
                        surpriseMe = surpriseMe,
                        onSelectionChanged = { cuisinesSelected = it },
                        onSurpriseMeChanged = { surpriseMe = it }
                    )
                    7 -> StepDietaryRestrictions(
                        vegetarian = vegetarian,
                        vegan = vegan,
                        dairyFree = dairyFree,
                        glutenFree = glutenFree,
                        noPork = noPork,
                        onVegChanged = { vegetarian = it },
                        onVeganChanged = { vegan = it },
                        onDairyChanged = { dairyFree = it },
                        onGlutenChanged = { glutenFree = it },
                        onPorkChanged = { noPork = it }
                    )
                    8 -> StepMaxCookTime(maxCookTime) { maxCookTime = it }
                    9 -> StepCookingSkill(skillLevel) { skillLevel = it }
                }
            }

            // 3. Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()
                            step--
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = InkPrimary
                        ),
                        modifier = Modifier
                            .height(52.dp)
                            .weight(1f)
                            .testTag("onboarding_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Zurück", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                val canContinue = when (step) {
                    5 -> mealTypesIncluded.isNotEmpty()
                    2 -> budgetOption != "custom" || customBudget.isNotEmpty()
                    else -> true
                }

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (step < totalSteps) {
                            step++
                        } else {
                            onComplete(
                                UserPreferences(
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
                        }
                    },
                    enabled = canContinue,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(52.dp)
                        .weight(1.5f)
                        .testTag("onboarding_next_button")
                ) {
                    Text(
                        if (step == totalSteps) "Plan erstellen" else "Weiter",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Weiter")
                }
            }
        }
    }
}

@Composable
fun StepHouseholdSize(selected: Int, onSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Wie groß ist dein Haushalt?",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Das hilft uns, die Portionsgrößen und Zutatenmengen präzise zu berechnen.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..5).forEach { num ->
                val isSelected = selected == num
                val label = if (num == 5) "5+" else num.toString()
                
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { onSelected(num) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSelected) Color.White else InkPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StepWeeklyBudget(
    budgetOption: String,
    customBudget: String,
    onOptionSelected: (String) -> Unit,
    onCustomValueChange: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Wöchentliches Budget?",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Gib dein ungefähres Budget für Lebensmittel pro Woche an. Wir optimieren die Rezeptauswahl darauf.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))

        val options = listOf(
            "50" to "€50 (Sparsam)",
            "75" to "€75 (Normal)",
            "100" to "€100 (Ausgewogen)",
            "150" to "€150 (Premium)"
        )

        options.forEach { (value, label) ->
            val isSelected = budgetOption == value
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onOptionSelected(value) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onOptionSelected(value) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        val isCustomSelected = budgetOption == "custom"
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isCustomSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onOptionSelected("custom") }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isCustomSelected,
                        onClick = { onOptionSelected("custom") },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Eigener Betrag (€)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkPrimary,
                        fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
                
                if (isCustomSelected) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customBudget,
                        onValueChange = onCustomValueChange,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("Z.B. 120") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StepSupermarket(selected: Supermarket, onSelected: (Supermarket) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Wo kaufst du meistens ein?",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Wir richten Rezepte und Preisschätzungen nach deinem bevorzugten Supermarkt aus.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        val options = listOf(
            Supermarket.MIX to "🛒 Bunt gemischt",
            Supermarket.LIDL to "Lidl",
            Supermarket.ALDI to "Aldi",
            Supermarket.REWE to "REWE",
            Supermarket.EDEKA to "Edeka",
            Supermarket.KAUFLAND to "Kaufland",
            Supermarket.PENNY to "Penny",
            Supermarket.NETTO to "Netto"
        )

        options.forEach { (market, label) ->
            val isSelected = selected == market
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelected(market) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelected(market) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun StepDaysToPlan(selected: Int, onSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Wie viele Tage planen?",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Für wie viele Tage möchtest du deinen Wochenzettel generieren?",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(3, 5, 7).forEach { num ->
                val isSelected = selected == num
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { onSelected(num) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = num.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            color = if (isSelected) Color.White else InkPrimary,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Tage",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else InkMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepMealTypes(selected: Set<MealType>, onSelected: (Set<MealType>) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Welche Mahlzeiten?",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Wähle die Mahlzeiten, die wir für dich planen sollen.\n(Erfordert mindestens eine Auswahl)",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))

        val meals = listOf(
            MealType.BREAKFAST to "Frühstück",
            MealType.LUNCH to "Mittagessen",
            MealType.SNACK to "Snack / Zwischenmahlzeit",
            MealType.DINNER to "Abendessen"
        )

        meals.forEach { (type, label) ->
            val isChecked = selected.contains(type)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        val next = if (isChecked) selected - type else selected + type
                        onSelected(next)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkPrimary,
                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                    )
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            val next = if (isChecked) selected - type else selected + type
                            onSelected(next)
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StepCuisines(
    selected: Set<Cuisine>,
    surpriseMe: Boolean,
    onSelectionChanged: (Set<Cuisine>) -> Unit,
    onSurpriseMeChanged: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Kulinarische Vorlieben",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Welche Küchen bevorzugst du? Keine Auswahl bedeutet \"Bunt gemischt\".",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

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
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = 3
        ) {
            cuisines.forEach { (cuisine, label) ->
                val isChecked = selected.contains(cuisine)
                FilterChip(
                    selected = isChecked,
                    onClick = {
                        val next = if (isChecked) selected - cuisine else selected + cuisine
                        onSelectionChanged(next)
                    },
                    label = { Text(label, fontWeight = FontWeight.Medium) },
                    modifier = Modifier.padding(4.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (selected.isNotEmpty()) {
            Spacer(modifier = Modifier.height(30.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (surpriseMe) MustardGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "🎲 Manchmal überraschen",
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Streut gelegentlich 1-2 Wildcard-Tage anderer Küchen ein, um Abwechslung zu garantieren.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = surpriseMe,
                        onCheckedChange = onSurpriseMeChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MustardGold,
                            checkedTrackColor = MustardGold.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StepDietaryRestrictions(
    vegetarian: Boolean,
    vegan: Boolean,
    dairyFree: Boolean,
    glutenFree: Boolean,
    noPork: Boolean,
    onVegChanged: (Boolean) -> Unit,
    onVeganChanged: (Boolean) -> Unit,
    onDairyChanged: (Boolean) -> Unit,
    onGlutenChanged: (Boolean) -> Unit,
    onPorkChanged: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Ernährungsform",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Wähle deine Diät- oder Unverträglichkeitsfilter. (Optional)",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        val list = listOf(
            Triple("Vegetarisch", vegetarian, onVegChanged),
            Triple("Vegan", vegan, onVeganChanged),
            Triple("Laktosefrei / Milchfrei", dairyFree, onDairyChanged),
            Triple("Glutenfrei (Best Effort)", glutenFree, onGlutenChanged),
            Triple("Kein Schweinefleisch / Halal", noPork, onPorkChanged)
        )

        list.forEach { (label, checked, onChanged) ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onChanged(!checked) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = InkPrimary,
                        fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal
                    )
                    Switch(
                        checked = checked,
                        onCheckedChange = onChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun StepMaxCookTime(selected: Int, onSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Maximale Kochzeit?",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Wie viel Zeit möchtest du maximal pro Mahlzeit in der Küche verbringen?",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(15, 30, 45, 60).forEach { mins ->
                val isSelected = selected == mins
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable { onSelected(mins) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = mins.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isSelected) Color.White else InkPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Min",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else InkMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StepCookingSkill(selected: Int, onSelected: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Deine Kocherfahrung?",
            style = MaterialTheme.typography.displayMedium,
            color = InkPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Das hilft uns, den Schwierigkeitsgrad der Rezepte anzupassen.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(30.dp))

        val skills = listOf(
            1 to ("Anfänger" to "Einfache, gelingsichere Rezepte mit wenigen Arbeitsschritten."),
            2 to ("Fortgeschritten" to "Normale Haushaltsküche mit grundlegenden Fertigkeiten."),
            3 to ("Profi" to "Anspruchsvollere Zubereitungen, die etwas Übung erfordern.")
        )

        skills.forEach { (level, details) ->
            val (title, desc) = details
            val isSelected = selected == level
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onSelected(level) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelected(level) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = InkPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted,
                        modifier = Modifier.padding(start = 40.dp)
                    )
                }
            }
        }
    }
}

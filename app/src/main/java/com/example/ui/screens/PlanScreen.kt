package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    weeklyPlan: WeeklyPlan?,
    householdSize: Int,
    isGenerating: Boolean,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMealForDetail by remember { mutableStateOf<ScaledRecipe?>(null) }

    AnimatedContent(
        targetState = selectedMealForDetail,
        transitionSpec = {
            if (targetState != null) {
                // Navigate forward (Slide in from right)
                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> -width } + fadeOut()
                )
            } else {
                // Navigate back (Slide out to right)
                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                    slideOutHorizontally { width -> width } + fadeOut()
                )
            }
        },
        label = "MealDetailAnimation"
    ) { activeRecipeForDetail ->
        if (activeRecipeForDetail != null) {
            MealDetailView(
                scaledRecipe = activeRecipeForDetail,
                householdSize = householdSize,
                onBack = { selectedMealForDetail = null }
            )
        } else {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "WOCHENPLAN",
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = InkPrimary
                            )
                        },
                        actions = {
                            if (!isGenerating && weeklyPlan != null) {
                                IconButton(
                                    onClick = onRegenerate,
                                    modifier = Modifier.testTag("regenerate_button")
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Plan neu generieren",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                modifier = modifier
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isGenerating) {
                        GenerationLoadingView()
                    } else if (weeklyPlan == null || weeklyPlan.days.isEmpty()) {
                        PlanEmptyState(onRegenerate)
                    } else {
                        PlanDashboardList(
                            weeklyPlan = weeklyPlan,
                            onMealClick = { selectedMealForDetail = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GenerationLoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Wochenzettel wird geschrieben...",
            style = MaterialTheme.typography.titleMedium,
            color = InkPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Gemini berechnet die optimalen Rezepte für deinen Geldbeutel und deinen Haushalt.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PlanEmptyState(onGenerate: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "📅",
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            "Noch kein Plan vorhanden",
            style = MaterialTheme.typography.titleLarge,
            color = InkPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Lass uns einen maßgeschneiderten Wochenzettel für dich erstellen.",
            style = MaterialTheme.typography.bodyMedium,
            color = InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onGenerate,
            modifier = Modifier.testTag("generate_initial_plan_button")
        ) {
            Text("Plan generieren", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PlanDashboardList(
    weeklyPlan: WeeklyPlan,
    onMealClick: (ScaledRecipe) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (weeklyPlan.appliedSwap != null) {
            item {
                BudgetSwapBanner(swap = weeklyPlan.appliedSwap)
            }
        }

        items(weeklyPlan.days) { day ->
            DaySection(day = day, onMealClick = onMealClick)
        }
    }
}

@Composable
fun BudgetSwapBanner(swap: BudgetSwap) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MustardGold.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔁",
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                val mealTypeName = when (swap.mealType) {
                    MealType.BREAKFAST -> "Frühstück"
                    MealType.LUNCH -> "Mittagessen"
                    MealType.SNACK -> "Snack"
                    MealType.DINNER -> "Abendessen"
                }
                Text(
                    text = "Budget-Optimierung durchgeführt!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MustardGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Für den ${swap.dayName} ($mealTypeName) haben wir das teure Gericht \"${swap.fromRecipeName}\" durch das günstigere Gericht \"${swap.toRecipeName}\" ersetzt. Du sparst dadurch wöchentlich ${String.format(Locale.GERMANY, "€%.2f", swap.savingsEUR)}!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkPrimary
                )
            }
        }
    }
}

@Composable
fun DaySection(
    day: PlannedDay,
    onMealClick: (ScaledRecipe) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = day.dayName.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        val activeMeals = listOf(
            MealType.BREAKFAST to day.breakfast,
            MealType.LUNCH to day.lunch,
            MealType.SNACK to day.snack,
            MealType.DINNER to day.dinner
        ).filter { it.second != null }

        if (activeMeals.isEmpty()) {
            Text(
                "Keine Mahlzeiten geplant.",
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted,
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                activeMeals.forEach { (mType, scaledRecipe) ->
                    if (scaledRecipe != null) {
                        MealCard(
                            mealType = mType,
                            scaledRecipe = scaledRecipe,
                            onClick = { onMealClick(scaledRecipe) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealCard(
    mealType: MealType,
    scaledRecipe: ScaledRecipe,
    onClick: () -> Unit
) {
    val mealTypeName = when (mealType) {
        MealType.BREAKFAST -> "Frühstück"
        MealType.LUNCH -> "Mittagessen"
        MealType.SNACK -> "Snack"
        MealType.DINNER -> "Abendessen"
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("meal_card_${scaledRecipe.recipe.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Big Emoji Circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaperBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = scaledRecipe.recipe.emoji,
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mealTypeName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = scaledRecipe.recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = InkPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = "Zeit",
                            tint = InkMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${scaledRecipe.recipe.cookTimeMinutes} Min",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = "Eiweiß",
                            tint = InkMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${scaledRecipe.recipe.proteinGrams}g Protein",
                            style = MaterialTheme.typography.labelSmall,
                            color = InkMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Cost tag
            Surface(
                color = PaperBg,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = String.format(Locale.GERMANY, "€%.2f", scaledRecipe.scaledCostEUR),
                    style = MaterialTheme.typography.labelSmall,
                    color = InkPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailView(
    scaledRecipe: ScaledRecipe,
    householdSize: Int,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = PaperBg, // Warm textured paper bg
        topBar = {
            TopAppBar(
                title = { Text("Details", fontWeight = FontWeight.Bold, color = InkPrimary) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("recipe_detail_back")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = InkPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(bottom = 32.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Hero Card with Emoji and Title
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(CardBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = scaledRecipe.recipe.emoji,
                            fontSize = 56.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = scaledRecipe.recipe.name,
                        style = MaterialTheme.typography.displayLarge,
                        color = InkPrimary,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(scaledRecipe.recipe.cuisine.name) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = InkMuted
                            )
                        )
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    when (scaledRecipe.recipe.mealType) {
                                        MealType.BREAKFAST -> "FRÜHSTÜCK"
                                        MealType.LUNCH -> "MITTAGESSEN"
                                        MealType.SNACK -> "SNACK"
                                        MealType.DINNER -> "ABENDESSEN"
                                    }
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = InkMuted
                            )
                        )
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val stats = listOf(
                        Triple("${scaledRecipe.recipe.cookTimeMinutes} Min", "Kochzeit", Icons.Default.AccessTime),
                        Triple("${scaledRecipe.recipe.proteinGrams}g", "Protein", Icons.Default.FitnessCenter),
                        Triple("${scaledRecipe.recipe.kcal} kcal", "Kalorien", null),
                        Triple(String.format(Locale.GERMANY, "€%.2f", scaledRecipe.scaledCostEUR), "Kosten", null)
                    )

                    stats.forEach { (value, label, icon) ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (icon != null) {
                                    Icon(
                                        icon,
                                        contentDescription = label,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = InkPrimary,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Scaled Ingredients Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Zutaten",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = InkPrimary
                        )
                        Text(
                            text = "skaliert für $householdSize ${if (householdSize == 1) "Person" else "Personen"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            scaledRecipe.scaledIngredients.forEach { ing ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ing.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = InkPrimary
                                    )
                                    val formattedQty = if (ing.quantity == 0.0) "" else if (ing.quantity % 1.0 == 0.0) ing.quantity.toInt().toString() else String.format(Locale.GERMANY, "%.1f", ing.quantity)
                                    Text(
                                        text = "$formattedQty ${ing.unit}",
                                        style = ReceiptTextStyles.Price,
                                        color = InkPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(color = HairlineColor.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            // Steps Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Zubereitung",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = InkPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    scaledRecipe.recipe.instructions.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyLarge,
                                color = InkPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    weeklyPlan: WeeklyPlan?,
    weeklyBudget: Double,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "BUDGETOPTIMIERUNG",
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
        if (weeklyPlan == null || weeklyPlan.days.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("💶", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Noch keine Budgetdaten",
                    style = MaterialTheme.typography.titleLarge,
                    color = InkPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Generiere zuerst einen Wochenplan, um die Kostenanalyse anzuzeigen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val totalCost = weeklyPlan.totalCostEUR
            val isOverBudget = totalCost > weeklyBudget
            val budgetRatio = (totalCost / weeklyBudget).toFloat().coerceIn(0f, 1f)

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 1. Spent vs. Budget Hero Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_hero_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                "WOCHEN-KASSENZETTEL",
                                style = MaterialTheme.typography.labelSmall,
                                color = InkMuted,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = String.format(Locale.GERMANY, "€%.2f", totalCost),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = if (isOverBudget) BrickRed else PrimaryGreen,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Geschätzte Gesamtkosten",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = InkMuted
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isOverBudget) BrickRed.copy(alpha = 0.15f)
                                            else PrimaryGreen.copy(alpha = 0.15f)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isOverBudget) "ÜBER BUDGET" else "IM BUDGET",
                                        color = if (isOverBudget) BrickRed else PrimaryGreen,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Progress Bar
                            LinearProgressIndicator(
                                progress = { budgetRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = if (isOverBudget) BrickRed else PrimaryGreen,
                                trackColor = HairlineColor.copy(alpha = 0.3f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "0 €",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkMuted
                                )
                                Text(
                                    text = String.format(Locale.GERMANY, "Budget: €%.2f", weeklyBudget),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = InkMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 2. Applied Budget Swap Card
                if (weeklyPlan.appliedSwap != null) {
                    item {
                        val swap = weeklyPlan.appliedSwap
                        val mealTypeName = when (swap.mealType) {
                            MealType.BREAKFAST -> "Frühstück"
                            MealType.LUNCH -> "Mittagessen"
                            MealType.SNACK -> "Snack"
                            MealType.DINNER -> "Abendessen"
                        }
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MustardGold.copy(alpha = 0.12f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔄", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                                    Text(
                                        text = "SPAR-WECHSEL ERFOLGT",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MustardGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Das System hat automatisch ein teures Gericht (${swap.fromRecipeName}) am ${swap.dayName} ($mealTypeName) durch ein günstigeres Gericht (${swap.toRecipeName}) ausgetauscht.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = String.format(Locale.GERMANY, "Eingesparte Summe: +€%.2f", swap.savingsEUR),
                                    style = ReceiptTextStyles.Price,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 3. Per-Meal Cost Breakdown List
                item {
                    Text(
                        text = "GERICHTE KOSTEN-AUFSTELLUNG",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkMuted,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                val allPlannedMeals = mutableListOf<MealCostBreakdown>()
                weeklyPlan.days.forEach { day ->
                    listOf(
                        MealType.BREAKFAST to day.breakfast,
                        MealType.LUNCH to day.lunch,
                        MealType.SNACK to day.snack,
                        MealType.DINNER to day.dinner
                    ).forEach { (type, scaled) ->
                        if (scaled != null) {
                            val mealLabel = when (type) {
                                MealType.BREAKFAST -> "Frühstück"
                                MealType.LUNCH -> "Mittags"
                                MealType.SNACK -> "Snack"
                                MealType.DINNER -> "Abends"
                            }
                            allPlannedMeals.add(
                                MealCostBreakdown(
                                    dayName = day.dayName,
                                    mealTypeLabel = mealLabel,
                                    recipeName = scaled.recipe.name,
                                    costEUR = scaled.scaledCostEUR
                                )
                            )
                        }
                    }
                }

                items(allPlannedMeals) { item ->
                    CostBreakdownRow(item = item)
                }
            }
        }
    }
}

@Composable
fun CostBreakdownRow(item: MealCostBreakdown) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.dayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${item.mealTypeLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkMuted
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.recipeName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = InkPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = String.format(Locale.GERMANY, "€%.2f", item.costEUR),
                style = ReceiptTextStyles.Price,
                color = InkPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class MealCostBreakdown(
    val dayName: String,
    val mealTypeLabel: String,
    val recipeName: String,
    val costEUR: Double
)

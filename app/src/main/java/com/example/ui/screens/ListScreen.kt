package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Aisle
import com.example.data.model.PantryItem
import com.example.data.model.WeeklyPlan
import com.example.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    weeklyPlan: WeeklyPlan?,
    weeklyBudget: Double,
    pantryItems: List<PantryItem>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "EINKAUFSZETTEL",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (weeklyPlan == null || weeklyPlan.days.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🧾", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Einkaufszettel ist leer",
                        style = MaterialTheme.typography.titleLarge,
                        color = InkPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Generiere zuerst einen Wochenplan, um deinen Einkaufszettel anzuzeigen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkMuted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                ReceiptTicket(
                    weeklyPlan = weeklyPlan,
                    weeklyBudget = weeklyBudget,
                    pantryItems = pantryItems
                )
            }
        }
    }
}

/**
 * Creates a generic shape with a scalloped top and bottom edge to simulate a torn paper receipt.
 */
val ScallopedReceiptShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    val scallopRadius = 15f
    val scallopWidth = scallopRadius * 2
    val numScallops = (size.width / scallopWidth).toInt()
    
    // Scalloped top edge
    for (i in 0 until numScallops) {
        val x = i * scallopWidth
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                left = x,
                top = -scallopRadius,
                right = x + scallopWidth,
                bottom = scallopRadius
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
    }
    // Line to bottom right
    lineTo(size.width, size.height)

    // Scalloped bottom edge
    for (i in numScallops - 1 downTo 0) {
        val x = i * scallopWidth
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                left = x,
                top = size.height - scallopRadius,
                right = x + scallopWidth,
                bottom = size.height + scallopRadius
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false
        )
    }
    close()
}

@Composable
fun ReceiptTicket(
    weeklyPlan: WeeklyPlan,
    weeklyBudget: Double,
    pantryItems: List<PantryItem>
) {
    // 1. Compile and aggregate ingredients
    val aggregatedIngredients = mutableMapOf<String, AggregatedIngredient>()

    weeklyPlan.days.forEach { day ->
        listOf(day.breakfast, day.lunch, day.snack, day.dinner).forEach { scaled ->
            if (scaled != null) {
                scaled.scaledIngredients.forEach { ing ->
                    val key = "${ing.name.lowercase().trim()}_${ing.unit.lowercase().trim()}"
                    val existing = aggregatedIngredients[key]
                    if (existing != null) {
                        aggregatedIngredients[key] = existing.copy(
                            quantityNeeded = existing.quantityNeeded + ing.quantity
                        )
                    } else {
                        aggregatedIngredients[key] = AggregatedIngredient(
                            name = ing.name,
                            unit = ing.unit,
                            quantityNeeded = ing.quantity,
                            aisle = ing.aisle
                        )
                    }
                }
            }
        }
    }

    // Map pantry items for quick subtraction lookup
    val pantryMap = pantryItems.associateBy { "${it.ingredientName.lowercase().trim()}_${it.unit.lowercase().trim()}" }

    val shoppingList = aggregatedIngredients.values.map { item ->
        val pantryKey = "${item.name.lowercase().trim()}_${item.unit.lowercase().trim()}"
        val owned = pantryMap[pantryKey]?.quantityOwned ?: 0.0
        val remaining = maxOf(0.0, item.quantityNeeded - owned)
        val fullyCovered = remaining <= 0.0
        
        ShoppingListItem(
            name = item.name,
            unit = item.unit,
            quantityNeeded = item.quantityNeeded,
            quantityOwned = owned,
            quantityToBuy = remaining,
            fullyCovered = fullyCovered,
            aisle = item.aisle
        )
    }

    // Group shopping list by aisle in specified order: Produce, Dairy, Meat, Pantry
    val displayAisleOrder = listOf(Aisle.PRODUCE, Aisle.DAIRY, Aisle.MEAT, Aisle.PANTRY)
    val groupedList = displayAisleOrder.associateWith { aisle ->
        shoppingList.filter { it.aisle == aisle }
    }.filterValues { it.isNotEmpty() }

    val isUnderBudget = weeklyPlan.totalCostEUR <= weeklyBudget

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ScallopedReceiptShape)
            .background(Color.White)
            .padding(vertical = 24.dp)
            .testTag("receipt_paper_ticket")
    ) {
        // Rotated Stamp Badge in top corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 20.dp)
                .rotate(9f)
        ) {
            RubberStampBadge(underBudget = isUnderBudget)
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Receipt Monospace Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "WOCHENZETTEL",
                        style = ReceiptTextStyles.Header,
                        color = InkPrimary,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "PLAN-ID: #${weeklyPlan.id}00${weeklyPlan.dateCreated % 1000}",
                        style = ReceiptTextStyles.Small,
                        color = InkMuted
                    )
                    Text(
                        text = "DEUTSCHER MARKT-ZETTEL",
                        style = ReceiptTextStyles.Small,
                        color = InkMuted
                    )
                    Text(
                        text = "--------------------------------",
                        style = ReceiptTextStyles.Body,
                        color = HairlineColor
                    )
                }
            }

            // Loop through grouped aisles
            groupedList.forEach { (aisle, items) ->
                item {
                    val aisleTitle = when (aisle) {
                        Aisle.PRODUCE -> "OBST & GEMÜSE"
                        Aisle.DAIRY -> "MILCHPRODUKTE"
                        Aisle.MEAT -> "FLEISCH & FISCH"
                        Aisle.PANTRY -> "VORRATSSCHRANK"
                    }
                    Text(
                        text = "[ $aisleTitle ]",
                        style = ReceiptTextStyles.Header.copy(fontSize = 13.sp),
                        color = InkPrimary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(items) { item ->
                    ReceiptItemRow(item = item)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Bottom Receipt Footer and Totals
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "--------------------------------",
                        style = ReceiptTextStyles.Body,
                        color = HairlineColor
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GESAMT SCHÄTZUNG",
                            style = ReceiptTextStyles.Header.copy(fontSize = 15.sp),
                            color = InkPrimary
                        )
                        Text(
                            text = String.format(Locale.GERMANY, "EUR %.2f", weeklyPlan.totalCostEUR),
                            style = ReceiptTextStyles.Header.copy(fontSize = 16.sp),
                            color = InkPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BUDGETVORGABE",
                            style = ReceiptTextStyles.Small,
                            color = InkMuted
                        )
                        Text(
                            text = String.format(Locale.GERMANY, "EUR %.2f", weeklyBudget),
                            style = ReceiptTextStyles.Small,
                            color = InkMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "* Vorhandener Vorrat abgezogen.\n* Preise sind Schätzwerte für Deutschland.",
                        style = ReceiptTextStyles.Small.copy(fontSize = 10.sp),
                        color = InkMuted,
                        textAlign = TextAlign.Start
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "DANKE FÜR DEINEN EINKAUF!",
                        style = ReceiptTextStyles.Header.copy(fontSize = 12.sp),
                        color = InkMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun RubberStampBadge(underBudget: Boolean) {
    val text = if (underBudget) "UNTER BUDGET" else "ÜBER BUDGET"
    val color = if (underBudget) PrimaryGreen else BrickRed
    
    Surface(
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(3.dp, color.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = text,
            color = color.copy(alpha = 0.8f),
            fontWeight = FontWeight.Black,
            fontFamily = MonospaceFamily,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ReceiptItemRow(item: ShoppingListItem) {
    val isOwned = item.fullyCovered
    val opacity = if (isOwned) 0.4f else 1.0f
    val textDecoration = if (isOwned) TextDecoration.LineThrough else TextDecoration.None

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Item Name
        Text(
            text = if (isOwned) "[✓] ${item.name}" else item.name,
            style = ReceiptTextStyles.Body,
            color = InkPrimary.copy(alpha = opacity),
            textDecoration = textDecoration,
            modifier = Modifier.widthIn(max = 160.dp)
        )
        
        // Custom Dotted-Leader line Spacer (§7)
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .padding(horizontal = 4.dp)
                .drawBehind {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 6f), 0f)
                    drawLine(
                        color = HairlineColor.copy(alpha = opacity),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 2f,
                        pathEffect = pathEffect
                    )
                }
        )

        // Quantity Needed vs Bought
        val formatQty = { qty: Double ->
            if (qty % 1.0 == 0.0) qty.toInt().toString() else String.format(Locale.GERMANY, "%.1f", qty)
        }

        val qtyText = if (isOwned) {
            "hast du (${formatQty(item.quantityNeeded)} ${item.unit})"
        } else {
            val toBuy = formatQty(item.quantityToBuy)
            val total = formatQty(item.quantityNeeded)
            if (item.quantityOwned > 0.0) {
                "$toBuy / $total ${item.unit}"
            } else {
                "$toBuy ${item.unit}"
            }
        }

        Text(
            text = qtyText,
            style = ReceiptTextStyles.Body,
            color = InkPrimary.copy(alpha = opacity),
            textDecoration = textDecoration,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

// Helper models for aggregation

data class AggregatedIngredient(
    val name: String,
    val unit: String,
    val quantityNeeded: Double,
    val aisle: Aisle
)

data class ShoppingListItem(
    val name: String,
    val unit: String,
    val quantityNeeded: Double,
    val quantityOwned: Double,
    val quantityToBuy: Double,
    val fullyCovered: Boolean,
    val aisle: Aisle
)

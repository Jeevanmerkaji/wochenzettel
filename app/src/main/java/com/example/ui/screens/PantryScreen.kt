package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PantryItem
import com.example.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantryScreen(
    pantryItems: List<PantryItem>,
    onAddItem: (String, Double, String) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf("") }
    var qtyInput by remember { mutableStateOf("") }
    var unitSelected by remember { mutableStateOf("g") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val units = listOf("g", "kg", "ml", "l", "Stück", "Scheibe", "Bund", "Dose", "Knoblauchzehe")

    val quickAddItems = listOf(
        Triple("Ei", 6.0, "Stück"),
        Triple("Milch", 1000.0, "ml"),
        Triple("Butter", 250.0, "g"),
        Triple("Gouda", 200.0, "g"),
        Triple("Vollkornbrot", 10.0, "Scheibe"),
        Triple("Kartoffeln", 1000.0, "g"),
        Triple("Nudeln", 500.0, "g"),
        Triple("Apfel", 4.0, "Stück"),
        Triple("Möhre", 5.0, "Stück"),
        Triple("Reis", 500.0, "g")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "VORRATSKAMMER",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = InkPrimary
                    )
                },
                actions = {
                    if (pantryItems.isNotEmpty()) {
                        TextButton(
                            onClick = onClearAll,
                            colors = ButtonDefaults.textButtonColors(contentColor = BrickRed)
                        ) {
                            Text("Alles leeren", fontWeight = FontWeight.Bold)
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
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Quick Add Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SCHNELL-HINZUFÜGEN",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkMuted,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickAddItems.forEach { (name, qty, unit) ->
                            val qtyLabel = if (qty % 1.0 == 0.0) qty.toInt().toString() else qty.toString()
                            FilterChip(
                                selected = false,
                                onClick = { onAddItem(name, qty, unit) },
                                label = { Text("$name (+$qtyLabel$unit)") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Hinzufügen",
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = CardBg,
                                    labelColor = InkPrimary
                                )
                            )
                        }
                    }
                }
            }

            // 2. Custom Add Entry Form
            item {
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
                        Text(
                            text = "ZUTAT MANUELL EINTRAGEN",
                            style = MaterialTheme.typography.labelLarge,
                            color = InkMuted,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Zutat Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1.8f)
                                    .testTag("pantry_name_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )

                            OutlinedTextField(
                                value = qtyInput,
                                onValueChange = { qtyInput = it },
                                label = { Text("Menge") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pantry_qty_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedButton(
                                    onClick = { dropdownExpanded = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                ) {
                                    Text("Einheit: $unitSelected", color = InkPrimary)
                                }
                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false }
                                ) {
                                    units.forEach { unit ->
                                        DropdownMenuItem(
                                            text = { Text(unit) },
                                            onClick = {
                                                unitSelected = unit
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            val isFormValid = nameInput.trim().isNotEmpty() && qtyInput.toDoubleOrNull() != null
                            
                            Button(
                                onClick = {
                                    val qty = qtyInput.toDoubleOrNull() ?: 0.0
                                    onAddItem(nameInput.trim(), qty, unitSelected)
                                    nameInput = ""
                                    qtyInput = ""
                                },
                                enabled = isFormValid,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .testTag("pantry_add_submit"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Zufügen")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hinzufügen")
                            }
                        }
                    }
                }
            }

            // 3. Pantry List Section
            if (pantryItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🧺", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Deine Vorratskammer ist leer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InkPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Füge Zutaten hinzu, die du bereits zu Hause hast. Wir ziehen sie automatisch von deinem Einkaufszettel ab!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "MEIN VORRAT (${pantryItems.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkMuted,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(pantryItems) { item ->
                    PantryItemRow(
                        item = item,
                        onDelete = { onDeleteItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PantryItemRow(
    item: PantryItem,
    onDelete: () -> Unit
) {
    val formattedQty = if (item.quantityOwned % 1.0 == 0.0) item.quantityOwned.toInt().toString() else String.format(Locale.GERMANY, "%.1f", item.quantityOwned)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("pantry_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.LocalMall,
                    contentDescription = "Vorratszutat",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.ingredientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkPrimary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "$formattedQty ${item.unit}",
                    style = ReceiptTextStyles.Price,
                    color = InkPrimary,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Zutat löschen",
                        tint = BrickRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

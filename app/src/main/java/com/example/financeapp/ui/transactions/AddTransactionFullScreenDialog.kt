@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.financeapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.financeapp.data.TransactionType

@Composable
fun AddTransactionFullScreenDialog(
    onDismissRequest: () -> Unit,
    onSave: (amount: Double, category: String, type: TransactionType) -> Unit
) {
    var amountText by rememberSaveable { mutableStateOf("") }

    val categories = listOf("Jedzenie", "Rachunki", "Rozrywka", "Transport", "Inne")
    var selectedCategory by rememberSaveable { mutableStateOf(categories.first()) }
    var isCategoryExpanded by rememberSaveable { mutableStateOf(false) }

    val types = TransactionType.entries
    var selectedTypeIndex by rememberSaveable { mutableIntStateOf(0) } // 0 = EXPENSE, 1 = INCOME

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Fullsize
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Add transaction") },
                        navigationIcon = {
                            IconButton(onClick = onDismissRequest) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close"
                                )
                            }
                        },
                        actions = {
                            TextButton(
                                onClick = {
                                    val parsedAmount = amountText.toDoubleOrNull()
                                    if (parsedAmount != null && parsedAmount > 0) {
                                        onSave(
                                            parsedAmount,
                                            selectedCategory,
                                            types[selectedTypeIndex]
                                        )
                                        onDismissRequest()
                                    }
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        types.forEachIndexed { index, type ->
                            SegmentedButton(
                                selected = selectedTypeIndex == index,
                                onClick = { selectedTypeIndex = index },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = types.size
                                )
                            ) {
                                Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = isCategoryExpanded,
                        onExpandedChange = { isCategoryExpanded = !isCategoryExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isCategoryExpanded,
                            onDismissRequest = { isCategoryExpanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        isCategoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
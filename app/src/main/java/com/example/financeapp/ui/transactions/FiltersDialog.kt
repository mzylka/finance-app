@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.financeapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.categories.Category
import kotlin.time.Instant

@Composable
fun FiltersDialog(
    modifier: Modifier = Modifier,
    categories: List<Category> = emptyList(),
    types: List<TransactionType> = emptyList(),
    onDismissRequest: () -> Unit,
    onApply: () -> Unit
) {
    val dateFromPickerState = rememberDatePickerState()
    val dateToPickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var isFromDatePicker by remember { mutableStateOf(true) }

    var isCategoryExpanded by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableIntStateOf(0) }

    var isTypeExpanded by remember { mutableStateOf(false) }
    var selectedTransactionType by remember { mutableStateOf<TransactionType?>(null) }

    val selectedCategoryName = if (selectedCategoryId == 0) {
        "All Categories"
    } else {
        categories.find { it.id == selectedCategoryId }?.name ?: "All Categories"
    }

    val selectedTypeName = when (selectedTransactionType) {
        null -> "All"
        TransactionType.INCOME -> "Income"
        TransactionType.EXPENSE -> "Expense"
    }

    val selectedFromDate = dateFromPickerState.selectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toString()
    } ?: ""

    val selectedToDate = dateToPickerState.selectedDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toString()
    } ?: ""
    
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filters")
                OutlinedTextField(
                    value = selectedFromDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("From Date") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePicker = true
                                isFromDatePicker = true
                            }
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedToDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("To Date") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                showDatePicker = true
                                isFromDatePicker = false
                            }
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = isCategoryExpanded,
                    onExpandedChange = { isCategoryExpanded = !isCategoryExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategoryName,
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
                        DropdownMenuItem(
                            text = { Text("All Categories") },
                            onClick = {
                                selectedCategoryId = 0
                                isCategoryExpanded = false
                            }
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    isCategoryExpanded = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = isTypeExpanded,
                    onExpandedChange = { isTypeExpanded = !isTypeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedTypeName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Transaction Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isTypeExpanded,
                        onDismissRequest = { isTypeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                selectedTransactionType = null
                                isTypeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Income") },
                            onClick = {
                                selectedTransactionType = TransactionType.INCOME
                                isTypeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Expense") },
                            onClick = {
                                selectedTransactionType = TransactionType.EXPENSE
                                isTypeExpanded = false
                            }
                        )
                    }
                }

                TextButton(onClick = onApply) {
                    Text("Apply")
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = if (isFromDatePicker) dateFromPickerState else dateToPickerState)
        }
    }
}
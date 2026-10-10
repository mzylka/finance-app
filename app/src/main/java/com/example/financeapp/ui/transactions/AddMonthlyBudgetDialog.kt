package com.example.financeapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddMonthlyBudgetDialog(
    modifier: Modifier = Modifier,
    budgetAmount: Double = 0.00,
    onDismissRequest: () -> Unit,
    onSave: (monthlyBudget: Double) -> Unit
) {
    var amountText by rememberSaveable { mutableStateOf(budgetAmount.toString()) }

    Dialog(onDismissRequest) {
        Card(modifier = modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp)) {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monthly Budget") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Button(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val parsedAmount = amountText.toDoubleOrNull()
                            if (parsedAmount != null && parsedAmount > 0) {
                                onSave(parsedAmount)
                                onDismissRequest()
                            }
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddMonthlyBudgetDialogPreview() {
    AddMonthlyBudgetDialog(onDismissRequest = {}, onSave = {})
}
package com.example.financeapp.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.financeapp.data.TransactionType

@Composable
fun AddTransactionForm(modifier: Modifier = Modifier) {
    var description by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableDoubleStateOf(0.0) }
    var category by rememberSaveable { mutableStateOf(TransactionType.INCOME) }

    Column(modifier = modifier) {
        TextField(value = description, onValueChange = { description = it })
        TextField(value = amount.toString(), onValueChange = { amount = it.toDoubleOrNull() ?: 0.0 })
        //SegmentedButton(selected = if (category == TransactionCategory.INCOME) 0 else 1, onClick = { category = !category })
    }
}
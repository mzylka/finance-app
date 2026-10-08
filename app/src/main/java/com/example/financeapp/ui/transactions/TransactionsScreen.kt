package com.example.financeapp.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.data.Transaction
import com.example.financeapp.data.TransactionCategory
import com.example.financeapp.ui.theme.FinanceAppTheme
import java.time.LocalDate

@Composable
fun Transactions(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = viewModel()
) {
    var showDialog by rememberSaveable() { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = !showDialog }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TransactionComponent(
                transaction = Transaction(
                    1,
                    LocalDate.now().toString(),
                    "Test",
                    100.0,
                    TransactionCategory.INCOME
                ),
                modifier = Modifier.fillMaxWidth()
            )
            TransactionComponent(
                transaction = Transaction(
                    1,
                    LocalDate.now().toString(),
                    "Test",
                    -10.0,
                    TransactionCategory.EXPENSE
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDialog) {
        AddTransactionFullScreenDialog(
            onDismissRequest = { showDialog = false},
            onSave = { d, string, category -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPreview() {
    FinanceAppTheme {
        Transactions()
    }
}
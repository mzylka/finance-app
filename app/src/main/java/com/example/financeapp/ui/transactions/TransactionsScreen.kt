package com.example.financeapp.ui.transactions

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.financeapp.ui.theme.FinanceAppTheme

@Composable
fun Transactions(
    modifier: Modifier = Modifier,
    showMonthlyBudget: Boolean = false,
    onShowBudgetClick: () -> Unit = {},
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTransactionDialog by rememberSaveable() { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTransactionDialog = !showAddTransactionDialog }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(uiState.transactions) { transaction ->
                TransactionComponent(transaction)
            }
        }
    }

    if (showAddTransactionDialog) {
        AddTransactionFullScreenDialog(
            onDismissRequest = { showAddTransactionDialog = false},
            categories = uiState.categories,
            onSave = { amount, categoryId, type ->
                viewModel.addTransaction(amount, categoryId, type)
                showAddTransactionDialog = false
            }
        )
    }

    if (showMonthlyBudget) {
        AddMonthlyBudgetDialog(
            budgetAmount = uiState.budgetAmount,
            onDismissRequest = { onShowBudgetClick() },
            onSave = { viewModel.updateMonthlyBudget(it) }
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
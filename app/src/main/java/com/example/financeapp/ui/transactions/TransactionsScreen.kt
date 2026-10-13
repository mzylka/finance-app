package com.example.financeapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.financeapp.ui.theme.FinanceAppTheme

@Composable
fun Transactions(
    modifier: Modifier = Modifier,
    showMonthlyBudget: Boolean = false,
    onShowBudgetClick: () -> Unit = {},
    showFilters: Boolean = false,
    onShowFiltersClick: () -> Unit = {},
    showManageCategories: Boolean = false,
    onShowManageCategoriesClick: () -> Unit = {},
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
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(text = "Month. Budget: ${uiState.budgetAmount}")
                Text(text = "Total amount: ${uiState.totalAmountCurrentMonth}")
            }
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(4.dp))
            LazyColumn {
                items(uiState.transactions) { transaction ->
                    TransactionComponent(transaction)
                }
            }
        }
    }

    if (showAddTransactionDialog) {
        AddTransactionDialog(
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

    if (showFilters) {
        FiltersDialog(
            currentFilter = uiState.currentFilter,
            categories = uiState.categories,
            onDismissRequest = { onShowFiltersClick() },
            onApply = { startDate, endDate, categoryId, type ->
                viewModel.applyFilters(startDate, endDate, categoryId, type)
            },
            onClear = {
                viewModel.clearFilters()
            }
        )
    }

    if (showManageCategories) {
        ManageCategoriesDialog(
            categories = uiState.categories,
            onDismissRequest = { onShowManageCategoriesClick() },
            onAddCategory = { categoryName ->
                viewModel.addCategory(categoryName)
            },
            onDeleteCategory = { categoryId ->
                viewModel.deleteCategory(categoryId)
            }
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
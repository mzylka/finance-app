package com.example.financeapp.ui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Home(
    modifier: Modifier = Modifier
) {
    Column() {
        HomeSlot(
            title = "Last Transactions"
        ) {
            LastTransactions()
        }
        HomeSlot(
            title = "Budget"
        ) {
            BudgetGraph()
        }
    }
}

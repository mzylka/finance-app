package com.example.financeapp.ui.transactions

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onDismissRequest: () -> Unit = {},
    onShowBudgetClick: () -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = { Text("Change monthly budget") },
            onClick = { onShowBudgetClick() }
        )
        DropdownMenuItem(
            text = { Text("Filter") },
            onClick = { /* Do something... */ }
        )
    }
}
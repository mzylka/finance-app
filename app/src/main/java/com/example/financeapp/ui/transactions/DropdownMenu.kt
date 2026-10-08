package com.example.financeapp.ui.transactions

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DropdownMenu(
    modifier: Modifier = Modifier,
    expaned: Boolean = false,
    onDismissRequest: () -> Unit = {}
) {
    DropdownMenu(
        expanded = expaned,
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = { Text("Change monthly budget") },
            onClick = { /* Do something... */ }
        )
        DropdownMenuItem(
            text = { Text("Another one") },
            onClick = { /* Do something... */ }
        )
    }
}
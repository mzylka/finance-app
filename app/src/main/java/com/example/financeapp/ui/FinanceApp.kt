package com.example.financeapp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.financeapp.ui.menu.MainMenu
import kotlinx.serialization.Serializable

@Serializable
object MainMenuScreen {}

@Serializable
object TransactionsScreen {}

@Serializable
object StatisticScreen {}

@Composable
fun FinanceApp(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        MainMenu()
    }
}
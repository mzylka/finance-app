package com.example.financeapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            label = { Text("Home") },
            selected = true,
            onClick = { navController.navigate(MainMenuScreen) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        NavigationBarItem(
            label = { Text("Transactions") },
            selected = false,
            onClick = { navController.navigate(TransactionsScreen) },
            icon = { Icon(Icons.Filled.AttachMoney, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        NavigationBarItem(
            label = { Text("Statistics") },
            selected = false,
            onClick = { navController.navigate(StatisticScreen) },
            icon = { Icon(Icons.Filled.Analytics, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

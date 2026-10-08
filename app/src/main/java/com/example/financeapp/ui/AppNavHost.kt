package com.example.financeapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.financeapp.ui.menu.Home
import com.example.financeapp.ui.statistic.Statistics
import com.example.financeapp.ui.transactions.Transactions
import kotlinx.serialization.Serializable

@Serializable
object MainMenuScreen {}

@Serializable
object TransactionsScreen {}

@Serializable
object StatisticScreen {}

@Composable
fun AppNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = MainMenuScreen,
        modifier = modifier
    ) {
        composable<MainMenuScreen>() {
            Home()
        }
        composable<TransactionsScreen>() {
            Transactions()
        }
        composable<StatisticScreen>() {
            Statistics()
        }
    }
}
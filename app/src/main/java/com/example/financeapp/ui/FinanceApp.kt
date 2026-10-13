package com.example.financeapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun FinanceApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isTransactionScreen = navBackStackEntry?.destination?.hasRoute<TransactionsScreen>() == true
    val isStatisticScreen = navBackStackEntry?.destination?.hasRoute<StatisticScreen>() == true
    val isMenuScreen = navBackStackEntry?.destination?.hasRoute<MainMenuScreen>() == true
    val title = if (isTransactionScreen) "Transactions" else "Finance App"
    var showMonthlyBudget by rememberSaveable { mutableStateOf(false) }
    val onShowBudgetClick = { showMonthlyBudget = !showMonthlyBudget }
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val onShowFiltersClick = { showFilters = !showFilters }
    var showManageCategories by rememberSaveable { mutableStateOf(false) }
    val onShowManageCategoriesClick = { showManageCategories = !showManageCategories }

    Scaffold(
        topBar = {
            Topbar(
                title = title,
                showMenu = isTransactionScreen,
                onShowBudgetClick = onShowBudgetClick,
                onShowFiltersClick = onShowFiltersClick,
                onShowManageCategoriesClick = onShowManageCategoriesClick
            )
        },
        bottomBar = {
            BottomBar(
                isTransactionScreen = isTransactionScreen,
                isStatisticScreen = isStatisticScreen,
                isMenuScreen = isMenuScreen,
                navController = navController
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost(
                navController = navController,
                showMonthlyBudget = showMonthlyBudget,
                onShowBudgetClick = onShowBudgetClick,
                showFilters = showFilters,
                onShowFiltersClick = onShowFiltersClick,
                showManageCategories = showManageCategories,
                onShowManageCategoriesClick = onShowManageCategoriesClick
            )
        }
    }

}
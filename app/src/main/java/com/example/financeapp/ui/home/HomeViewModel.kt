package com.example.financeapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import com.example.financeapp.data.budget.BudgetRepository
import com.example.financeapp.data.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.Instant

data class HomeUiState(
    val monthlyBudget: BigDecimal = BigDecimal.ZERO,
    val currentMonthIncome: BigDecimal = BigDecimal.ZERO,
    val currentMonthExpense: BigDecimal = BigDecimal.ZERO,
    val recentTransactions: List<TransactionWithCategory> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private fun getCurrentMonthRange(): Pair<Instant, Instant> {
        val zoneId = ZoneId.systemDefault()
        val now = LocalDate.now(zoneId)
        val startOfMonth = Instant.fromEpochMilliseconds(
            now.withDayOfMonth(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        )
        val endOfMonth = Instant.fromEpochMilliseconds(
            now.withDayOfMonth(now.lengthOfMonth())
                .atTime(23, 59, 59, 999_000_000)
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli()
        )
        return Pair(startOfMonth, endOfMonth)
    }

    private val currentMonthRange = getCurrentMonthRange()

    private val currentMonthAmountsFlow = combine(
        transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.INCOME,
            currentMonthRange.first,
            currentMonthRange.second
        ),
        transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.EXPENSE,
            currentMonthRange.first,
            currentMonthRange.second
        )
    ) { income, expense ->
        Pair(income, expense)
    }

    val uiState: StateFlow<HomeUiState> = combine(
        budgetRepository.getBudget(),
        currentMonthAmountsFlow,
        transactionRepository.getRecentTransactionsWithCategoryFlow(5)
    ) { budget, monthAmounts, recentTransactions ->
        HomeUiState(
            monthlyBudget = budget.amount,
            currentMonthIncome = monthAmounts.first,
            currentMonthExpense = monthAmounts.second,
            recentTransactions = recentTransactions,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(isLoading = true)
    )
}

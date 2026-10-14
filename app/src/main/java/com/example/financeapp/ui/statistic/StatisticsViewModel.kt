package com.example.financeapp.ui.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.CategoryAmount
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

data class StatisticsUiState(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val currentMonthExpense: BigDecimal = BigDecimal.ZERO,
    val currentMonthIncome: BigDecimal = BigDecimal.ZERO,
    val monthlyBudget: BigDecimal = BigDecimal.ZERO,
    val categoryExpens: List<CategoryAmount> = emptyList(),
    val categoryIncomes: List<CategoryAmount> = emptyList(),
    val isLoading: Boolean = false
)

private data class Totals(
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val currentMonthExpense: BigDecimal,
    val currentMonthIncome: BigDecimal
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
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

    private val totalsFlow = combine(
        transactionRepository.getTotalAmountByType(TransactionType.INCOME),
        transactionRepository.getTotalAmountByType(TransactionType.EXPENSE),
        transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.EXPENSE,
            currentMonthRange.first,
            currentMonthRange.second
        ),
        transactionRepository.getTotalAmountByTypeAndDateRange(
            TransactionType.INCOME,
            currentMonthRange.first,
            currentMonthRange.second
        )
    ) { income, expense, monthExpense, monthIncome ->
        Totals(
            totalIncome = income,
            totalExpense = expense,
            currentMonthExpense = monthExpense,
            currentMonthIncome = monthIncome
        )
    }

    private val categoryBreakdownFlow = combine(
        transactionRepository.getCategoryAmountsByType(TransactionType.EXPENSE),
        transactionRepository.getCategoryAmountsByType(TransactionType.INCOME)
    ) { categoryExpenses, categoryIncomes ->
        Pair(categoryExpenses, categoryIncomes)
    }

    val uiState: StateFlow<StatisticsUiState> = combine(
        totalsFlow,
        categoryBreakdownFlow,
        budgetRepository.getBudget()
    ) { totals, categoryBreakdown, budget ->
        StatisticsUiState(
            totalIncome = totals.totalIncome,
            totalExpense = totals.totalExpense,
            currentMonthExpense = totals.currentMonthExpense,
            currentMonthIncome = totals.currentMonthIncome,
            monthlyBudget = budget.amount,
            categoryExpens = categoryBreakdown.first,
            categoryIncomes = categoryBreakdown.second,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsUiState(isLoading = true)
    )
}

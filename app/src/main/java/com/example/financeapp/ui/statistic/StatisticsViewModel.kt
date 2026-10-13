package com.example.financeapp.ui.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import com.example.financeapp.data.budget.BudgetRepository
import com.example.financeapp.data.categories.CategoryRepository
import com.example.financeapp.data.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.time.Instant

data class CategoryExpense(
    val categoryName: String,
    val totalExpense: BigDecimal
)

data class StatisticsUiState(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val currentMonthExpense: BigDecimal = BigDecimal.ZERO,
    val monthlyBudget: BigDecimal = BigDecimal.ZERO,
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = combine(
        transactionRepository.getTransactionsWithCategory(),
        categoryRepository.getAllCategories(),
        budgetRepository.getBudget()
    ) { transactionsWithCategory, _, budget ->
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

        val totalIncome = transactionsWithCategory
            .filter { it.transaction.type == TransactionType.INCOME }
            .fold(BigDecimal.ZERO) { acc, item -> acc + item.transaction.amount }

        val totalExpense = transactionsWithCategory
            .filter { it.transaction.type == TransactionType.EXPENSE }
            .fold(BigDecimal.ZERO) { acc, item -> acc + item.transaction.amount }

        val currentMonthExpense = transactionsWithCategory
            .filter {
                it.transaction.type == TransactionType.EXPENSE &&
                        it.transaction.date >= startOfMonth &&
                        it.transaction.date <= endOfMonth
            }
            .fold(BigDecimal.ZERO) { acc, item -> acc + item.transaction.amount }

        val categoryExpenses = transactionsWithCategory
            .filter { it.transaction.type == TransactionType.EXPENSE }
            .groupBy { it.category.name }
            .map { (categoryName, list) ->
                val total = list.fold(BigDecimal.ZERO) { acc, item -> acc + item.transaction.amount }
                CategoryExpense(categoryName = categoryName, totalExpense = total)
            }
            .sortedByDescending { it.totalExpense }

        StatisticsUiState(
            transactions = transactionsWithCategory,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            currentMonthExpense = currentMonthExpense,
            monthlyBudget = budget.amount,
            categoryExpenses = categoryExpenses,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsUiState(isLoading = true)
    )
}

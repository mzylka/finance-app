package com.example.financeapp.ui.statistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.CategoryAmount
import com.example.financeapp.data.MonthlyTotals
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import com.example.financeapp.data.budget.BudgetRepository
import com.example.financeapp.data.preferences.UserPreferencesRepository
import com.example.financeapp.data.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
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
    val monthlyTotals: List<MonthlyTotals> = emptyList(),
    val selectedCurrency: String = "$",
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
    private val budgetRepository: BudgetRepository,
    private val preferencesRepository: UserPreferencesRepository
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

    private fun getTwelveMonthsAgoStart(): Instant {
        val zoneId = ZoneId.systemDefault()
        val startOf12MonthsAgo = LocalDate.now(zoneId)
            .minusMonths(11)
            .withDayOfMonth(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
        return Instant.fromEpochMilliseconds(startOf12MonthsAgo)
    }

    private val twelveMonthsAgoStart = getTwelveMonthsAgoStart()

    private val last12MonthsList: List<Pair<String, String>> = buildList {
        val zoneId = ZoneId.systemDefault()
        val now = LocalDate.now(zoneId)
        val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())
        val ymFormatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.getDefault())
        for (i in 11 downTo 0) {
            val date = now.minusMonths(i.toLong()).withDayOfMonth(1)
            val yearMonthStr = date.format(ymFormatter)
            val monthLabelStr = date.format(monthFormatter)
            add(yearMonthStr to monthLabelStr)
        }
    }

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

    private val monthlyTotalsFlow: Flow<List<MonthlyTotals>> =
        transactionRepository.getMonthlyTotalsFromDate(twelveMonthsAgoStart).map { rawList ->
            val map = rawList.groupBy { it.yearMonth }
            last12MonthsList.map { (yearMonth, monthLabel) ->
                val entries = map[yearMonth] ?: emptyList()
                val income = entries.find { it.type == TransactionType.INCOME }?.totalAmount ?: BigDecimal.ZERO
                val expense = entries.find { it.type == TransactionType.EXPENSE }?.totalAmount ?: BigDecimal.ZERO
                MonthlyTotals(
                    yearMonth = yearMonth,
                    monthLabel = monthLabel,
                    income = income,
                    expense = expense
                )
            }
        }

    val uiState: StateFlow<StatisticsUiState> = combine(
        totalsFlow,
        categoryBreakdownFlow,
        budgetRepository.getBudget(),
        preferencesRepository.getCurrencySymbol(),
        monthlyTotalsFlow
    ) { totals, categoryBreakdown, budget, currency, monthlyTotals ->
        StatisticsUiState(
            totalIncome = totals.totalIncome,
            totalExpense = totals.totalExpense,
            currentMonthExpense = totals.currentMonthExpense,
            currentMonthIncome = totals.currentMonthIncome,
            monthlyBudget = budget.amount,
            categoryExpens = categoryBreakdown.first,
            categoryIncomes = categoryBreakdown.second,
            monthlyTotals = monthlyTotals,
            selectedCurrency = currency,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsUiState(isLoading = true)
    )
}

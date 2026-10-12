package com.example.financeapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import com.example.financeapp.data.budget.Budget
import com.example.financeapp.data.budget.BudgetRepository
import com.example.financeapp.data.categories.Category
import com.example.financeapp.data.categories.CategoryRepository
import com.example.financeapp.data.transactions.Transaction
import com.example.financeapp.data.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

data class TransactionState (
    val transactions: List<TransactionWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val budgetAmount: BigDecimal = BigDecimal.ZERO,
    val currentFilter: FilterState = FilterState()
)

data class FilterState(
    val searchQuery: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val categoryId: Int? = null,
    val type: TransactionType? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    private val filterState = MutableStateFlow(FilterState())

    val uiState: StateFlow<TransactionState> = combine(
        categoryRepository.getAllCategories(),
        filterState,
        budgetRepository.getBudget()
    ) { categories, filters, budget ->
        Triple(categories, filters, budget)
    }.flatMapLatest { (categories, filters, budget) ->
        val hasStartDate = filters.startDate != null
        val startDate = Instant.fromEpochMilliseconds(filters.startDate ?: 0L)

        val hasEndDate = filters.endDate != null
        val endDateMillis = filters.endDate?.let { it + 86_399_999L } ?: 0L //end of the day
        val endDate = Instant.fromEpochMilliseconds(endDateMillis)

        val isFilterByCategory = filters.categoryId != null && filters.categoryId > 0
        val categoryId = filters.categoryId ?: 0

        val isFilterByType = filters.type != null
        val filterType = filters.type ?: TransactionType.INCOME

        transactionRepository.getFilteredTransactionsWithCategory(
            hasStartDate = hasStartDate,
            startDate = startDate,
            hasEndDate = hasEndDate,
            endDate = endDate,
            isFilterByCategory = isFilterByCategory,
            categoryId = categoryId,
            isFilterByType = isFilterByType,
            filterType = filterType
        ).map { transactionsWithCategory ->
            val filtered = transactionsWithCategory.filter { item ->
                filters.searchQuery.isBlank() ||
                    item.category.name.contains(filters.searchQuery, ignoreCase = true) ||
                    item.transaction.amount.toString().contains(filters.searchQuery)
            }
            TransactionState(
                transactions = filtered,
                categories = categories,
                budgetAmount = budget.amount,
                currentFilter = filters
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransactionState()
    )

    fun setSearchQuery(query: String) {
        filterState.value = filterState.value.copy(searchQuery = query)
    }

    fun applyFilters(
        startDate: Long?,
        endDate: Long?,
        categoryId: Int?,
        type: TransactionType?
    ) {
        filterState.value = filterState.value.copy(
            startDate = startDate,
            endDate = endDate,
            categoryId = categoryId,
            type = type
        )
    }

    fun clearFilters() {
        filterState.value = filterState.value.copy(
            startDate = null,
            endDate = null,
            categoryId = null,
            type = null
        )
    }

    // User Actions
    fun addTransaction(amount: BigDecimal, categoryId: Int, type: TransactionType) {
        viewModelScope.launch {
            val roundedAmount = amount.setScale(2, RoundingMode.HALF_UP)
            val transaction = Transaction(
                id = 0,
                date = Clock.System.now(),
                categoryId = categoryId,
                amount = roundedAmount,
                type = type
            )
            transactionRepository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            transactionRepository.deleteTransactionById(id)
        }
    }

    fun updateTransactionAmount(id: Int, amount: BigDecimal) {
        viewModelScope.launch {
            val roundedAmount = amount.setScale(2, RoundingMode.HALF_UP)
            transactionRepository.updateTransactionAmount(id, roundedAmount)
        }
    }

    fun updateTransactionCategory(id: Int, categoryId: Int) {
        viewModelScope.launch {
            transactionRepository.updateTransactionCategory(id, categoryId)
        }
    }

    fun updateMonthlyBudget(amount: BigDecimal) {
        viewModelScope.launch {
            val roundedAmount = amount.setScale(2, RoundingMode.HALF_UP)
            val budget = Budget(id = 1, amount = roundedAmount)
            budgetRepository.insertBudget(budget)
        }
    }
}

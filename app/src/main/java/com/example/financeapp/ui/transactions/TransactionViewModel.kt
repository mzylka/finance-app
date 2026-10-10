package com.example.financeapp.ui.transactions

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

data class TransactionState (
    val transactions: List<TransactionWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val budgetAmount: Double = 0.00
)

data class FilterState(
    val searchQuery: String = "",
    val isFilterByDate: Boolean = false,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val isFilterByCategory: Boolean = false,
    val categoryId: Int = 0,
    val isFilterByType: Boolean = false,
    val type: TransactionType = TransactionType.INCOME
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")
    private val isFilterByDate = MutableStateFlow(false)
    private val filterStartDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())
    private val filterEndDate = MutableStateFlow(Clock.System.now().toEpochMilliseconds())

    private val isFilterByCategory = MutableStateFlow(false)
    private val filterCategory = MutableStateFlow(0)

    private val isFilterByTypes = MutableStateFlow(false)
    private val filterType = MutableStateFlow(TransactionType.INCOME)

    private val filterState = combine(
        combine(searchQuery, isFilterByDate, filterStartDate) { q, b, s -> Triple(q, b, s) },
        combine(filterEndDate, isFilterByCategory, filterCategory) { e, isCat, cat -> Triple(e, isCat, cat) },
        combine(isFilterByTypes, filterType) { isType, type -> Pair(isType, type) }
    ) { t1, t2, pair ->
        FilterState(
            searchQuery = t1.first,
            isFilterByDate = t1.second,
            startDate = t1.third,
            endDate = t2.first,
            isFilterByCategory = t2.second,
            categoryId = t2.third,
            isFilterByType = pair.first,
            type = pair.second
        )
    }

    val uiState: StateFlow<TransactionState> = combine(
        categoryRepository.getAllCategories(),
        filterState,
        budgetRepository.getBudget()
    ) { categories, filters, budget ->
        Triple(categories, filters, budget)
    }.flatMapLatest { (categories, filters, budget) ->
        val startDate = Instant.fromEpochMilliseconds(filters.startDate)
        val endDate = Instant.fromEpochMilliseconds(filters.endDate)

        transactionRepository.getFilteredTransactionsWithCategory(
            isFilterByDate = filters.isFilterByDate,
            startDate = startDate,
            endDate = endDate,
            isFilterByCategory = filters.isFilterByCategory,
            categoryId = filters.categoryId,
            isFilterByType = filters.isFilterByType,
            filterType = filters.type
        ).map { transactionsWithCategory ->
            val filtered = transactionsWithCategory.filter { item ->
                filters.searchQuery.isBlank() ||
                    item.category.name.contains(filters.searchQuery, ignoreCase = true) ||
                    item.transaction.amount.toString().contains(filters.searchQuery)
            }
            TransactionState(transactions = filtered, categories = categories, budgetAmount = budget.amount)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransactionState()
    )

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setDateFilter(enabled: Boolean, startDate: Long = filterStartDate.value, endDate: Long = filterEndDate.value) {
        isFilterByDate.value = enabled
        filterStartDate.value = startDate
        filterEndDate.value = endDate
    }

    fun setCategoryFilter(enabled: Boolean, categoryId: Int = filterCategory.value) {
        isFilterByCategory.value = enabled
        filterCategory.value = categoryId
    }

    fun setTypeFilter(enabled: Boolean, type: TransactionType = filterType.value) {
        isFilterByTypes.value = enabled
        filterType.value = type
    }

    // User Actions
    fun addTransaction(amount: Double, categoryId: Int, type: TransactionType) {
        viewModelScope.launch {
            val transaction = Transaction(
                id = 0,
                date = Clock.System.now(),
                categoryId = categoryId,
                amount = amount,
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

    fun updateTransactionAmount(id: Int, amount: Double) {
        viewModelScope.launch {
            transactionRepository.updateTransactionAmount(id, amount)
        }
    }

    fun updateTransactionCategory(id: Int, categoryId: Int) {
        viewModelScope.launch {
            transactionRepository.updateTransactionCategory(id, categoryId)
        }
    }

    fun updateMonthlyBudget(amount: Double) {
        viewModelScope.launch {
            val budget = Budget(id = 1, amount = amount)
            budgetRepository.insertBudget(budget)
        }
    }
}

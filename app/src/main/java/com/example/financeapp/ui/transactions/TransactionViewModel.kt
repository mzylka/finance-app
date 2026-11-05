package com.example.financeapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import com.example.financeapp.data.budget.Budget
import com.example.financeapp.data.budget.BudgetRepository
import com.example.financeapp.data.categories.Category
import com.example.financeapp.data.categories.CategoryRepository
import com.example.financeapp.data.preferences.UserPreferencesRepository
import com.example.financeapp.data.transactions.Transaction
import com.example.financeapp.data.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

data class TransactionState (
    val transactions: List<TransactionWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val budgetAmount: BigDecimal = BigDecimal.ZERO,
    val currentFilter: FilterState = FilterState(),
    val totalAmountCurrentMonth: BigDecimal = BigDecimal.ZERO,
    val selectedCurrency: String = "$",
    val categoryError: String? = null
)

data class FilterState(
    val startDate: Long? = null,
    val endDate: Long? = null,
    val categoryId: Int? = null,
    val type: TransactionType? = null
)

private data class CombinedData(
    val categories: List<Category>,
    val filters: FilterState,
    val budget: Budget,
    val monthTotal: BigDecimal,
    val currency: String,
    val categoryError: String?
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val filterState = MutableStateFlow(FilterState())
    private val categoryErrorState = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TransactionState> = combine(
        combine(
            categoryRepository.getAllCategories(),
            filterState,
            budgetRepository.getBudget(),
            transactionRepository.getCurrentMonthTotalAmount(),
            preferencesRepository.getCurrencySymbol()
        ) { categories, filters, budget, monthTotal, currency ->
            CombinedData(categories, filters, budget, monthTotal, currency, null)
        },
        categoryErrorState
    ) { combined, categoryError ->
        Pair(combined, categoryError)
    }.flatMapLatest { (combined, categoryError) ->
        val filters = combined.filters
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
            TransactionState(
                transactions = transactionsWithCategory,
                categories = combined.categories,
                budgetAmount = combined.budget.amount,
                currentFilter = filters,
                totalAmountCurrentMonth = combined.monthTotal,
                selectedCurrency = combined.currency,
                categoryError = categoryError
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransactionState()
    )

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
    fun addCategory(name: String) {
        viewModelScope.launch {
            val trimmedName = name.trim()
            if (trimmedName.isBlank()) {
                categoryErrorState.value = "Category name cannot be blank"
                return@launch
            }
            val existingCategories = uiState.value.categories
            if (existingCategories.any { it.name.equals(trimmedName, ignoreCase = true) }) {
                categoryErrorState.value = "Category already exists"
                return@launch
            }
            categoryErrorState.value = null
            categoryRepository.insertCategory(Category(id = 0, name = trimmedName))
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            val transactions = transactionRepository.getTransactionsByCategory(id)
            if (transactions.isNotEmpty()) {
                categoryErrorState.value = "Cannot delete category with active transactions"
                return@launch
            }
            categoryErrorState.value = null
            categoryRepository.deleteCategoryById(id)
        }
    }

    fun clearCategoryError() {
        categoryErrorState.value = null
    }

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

    fun updateMonthlyBudget(amount: BigDecimal) {
        viewModelScope.launch {
            val roundedAmount = amount.setScale(2, RoundingMode.HALF_UP)
            val budget = Budget(id = 1, amount = roundedAmount)
            budgetRepository.insertBudget(budget)
        }
    }

    fun exportTransactionsToCsv(outputStream: OutputStream): kotlinx.coroutines.Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val transactions = transactionRepository.getTransactionsWithCategory()
            outputStream.bufferedWriter().use { writer ->
                writer.appendLine("Date,Category,Amount,Type")
                for ((transaction, category) in transactions) {
                    writer.appendLine("${transaction.date},${category.name},${transaction.amount},${transaction.type}")
                }
            }
        }
    }
}

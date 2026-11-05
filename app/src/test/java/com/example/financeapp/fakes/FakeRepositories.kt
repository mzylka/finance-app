package com.example.financeapp.fakes

import com.example.financeapp.data.CategoryAmount
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import com.example.financeapp.data.budget.Budget
import com.example.financeapp.data.budget.BudgetRepository
import com.example.financeapp.data.categories.Category
import com.example.financeapp.data.categories.CategoryRepository
import com.example.financeapp.data.transactions.Transaction
import com.example.financeapp.data.transactions.TransactionRepository
import com.example.financeapp.data.backup.BackupRepository
import com.example.financeapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import java.io.OutputStream
import java.math.BigDecimal
import kotlin.time.Instant

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val currencyFlow = MutableStateFlow("$")

    override fun getCurrencySymbol(): Flow<String> = currencyFlow

    override suspend fun setCurrencySymbol(symbol: String) {
        currencyFlow.value = symbol
    }
}

class FakeBackupRepository : BackupRepository {
    var exportCalled = false
    var importCalled = false
    var shouldThrowError = false

    override suspend fun exportBackup(outputStream: OutputStream) {
        if (shouldThrowError) {
            throw java.io.IOException("Disk write error during export")
        }
        exportCalled = true
        outputStream.write("{}".toByteArray())
    }

    override suspend fun importBackup(inputStream: InputStream) {
        if (shouldThrowError) {
            throw java.io.IOException("Malformed JSON during import")
        }
        importCalled = true
    }
}

class FakeCategoryRepository : CategoryRepository {
    private val categoriesFlow = MutableStateFlow<List<Category>>(
        listOf(
            Category(1, "Food & Dining"),
            Category(2, "Transportation"),
            Category(3, "Salary")
        )
    )

    override fun getAllCategories(): Flow<List<Category>> = categoriesFlow

    override suspend fun getCategoryById(id: Int): Category? {
        return categoriesFlow.value.find { it.id == id }
    }

    override suspend fun insertCategory(category: Category) {
        val newCategory = category.copy(id = (categoriesFlow.value.maxOfOrNull { it.id } ?: 0) + 1)
        categoriesFlow.value = categoriesFlow.value + newCategory
    }

    override suspend fun deleteCategoryById(id: Int) {
        categoriesFlow.value = categoriesFlow.value.filter { it.id != id }
    }

    override suspend fun updateCategoryName(id: Int, name: String) {
        categoriesFlow.value = categoriesFlow.value.map {
            if (it.id == id) it.copy(name = name) else it
        }
    }
}

class FakeBudgetRepository : BudgetRepository {
    private val budgetFlow = MutableStateFlow(Budget(1, BigDecimal("1000.00")))

    override fun getBudget(): Flow<Budget> = budgetFlow

    override suspend fun insertBudget(budget: Budget) {
        budgetFlow.value = budget
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetFlow.value = budget
    }
}

class FakeTransactionRepository : TransactionRepository {
    private val transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())
    private val categories = listOf(
        Category(1, "Food & Dining"),
        Category(2, "Transportation"),
        Category(3, "Salary")
    )

    override fun getAllTransactions(): Flow<List<Transaction>> = transactionsFlow

    override suspend fun getTransactionById(id: Int): Transaction? {
        return transactionsFlow.value.find { it.id == id }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        val newTx = transaction.copy(id = (transactionsFlow.value.maxOfOrNull { it.id } ?: 0) + 1)
        transactionsFlow.value = transactionsFlow.value + newTx
    }

    override suspend fun deleteTransactionById(id: Int) {
        transactionsFlow.value = transactionsFlow.value.filter { it.id != id }
    }

    override suspend fun getTransactionsByDateRange(startDate: Instant, endDate: Instant): List<Transaction> {
        return transactionsFlow.value.filter { it.date >= startDate && it.date <= endDate }
    }

    override suspend fun getTransactionsByType(type: TransactionType): List<Transaction> {
        return transactionsFlow.value.filter { it.type == type }
    }

    override suspend fun getTransactionsByCategory(categoryId: Int): List<Transaction> {
        return transactionsFlow.value.filter { it.categoryId == categoryId }
    }

    override suspend fun getTransactionsByDatesAndCategory(startDate: Instant, endDate: Instant, categoryId: Int): List<Transaction> {
        return transactionsFlow.value.filter { it.date >= startDate && it.date <= endDate && it.categoryId == categoryId }
    }

    override fun getTransactionsWithCategoryFlow(): Flow<List<TransactionWithCategory>> {
        return transactionsFlow.map { list ->
            list.map { tx ->
                val cat = categories.find { it.id == tx.categoryId } ?: Category(0, "Unknown")
                TransactionWithCategory(tx, cat)
            }
        }
    }

    override suspend fun getTransactionsWithCategory(): List<TransactionWithCategory> {
        return transactionsFlow.value.map { tx ->
            val cat = categories.find { it.id == tx.categoryId } ?: Category(0, "Unknown")
            TransactionWithCategory(tx, cat)
        }
    }

    override fun getRecentTransactionsWithCategoryFlow(limit: Int): Flow<List<TransactionWithCategory>> {
        return getTransactionsWithCategoryFlow().map { list -> list.take(limit) }
    }

    override fun getFilteredTransactionsWithCategory(
        hasStartDate: Boolean,
        startDate: Instant,
        hasEndDate: Boolean,
        endDate: Instant,
        isFilterByCategory: Boolean,
        categoryId: Int,
        isFilterByType: Boolean,
        filterType: TransactionType
    ): Flow<List<TransactionWithCategory>> {
        return getTransactionsWithCategoryFlow().map { list ->
            list.filter { item ->
                (!hasStartDate || item.transaction.date >= startDate) &&
                (!hasEndDate || item.transaction.date <= endDate) &&
                (!isFilterByCategory || item.transaction.categoryId == categoryId) &&
                (!isFilterByType || item.transaction.type == filterType)
            }
        }
    }

    override suspend fun updateTransactionAmount(id: Int, amount: BigDecimal) {
        transactionsFlow.value = transactionsFlow.value.map {
            if (it.id == id) it.copy(amount = amount) else it
        }
    }

    override suspend fun updateTransactionCategory(id: Int, categoryId: Int) {
        transactionsFlow.value = transactionsFlow.value.map {
            if (it.id == id) it.copy(categoryId = categoryId) else it
        }
    }

    override suspend fun updateTransactionType(id: Int, type: TransactionType) {
        transactionsFlow.value = transactionsFlow.value.map {
            if (it.id == id) it.copy(type = type) else it
        }
    }

    override fun getCurrentMonthTotalAmount(): Flow<BigDecimal> {
        return transactionsFlow.map { list ->
            list.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.signedAmount }
        }
    }

    override fun getTotalAmountByType(type: TransactionType): Flow<BigDecimal> {
        return transactionsFlow.map { list ->
            list.filter { it.type == type }.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
        }
    }

    override fun getTotalAmountByTypeAndDateRange(type: TransactionType, startDate: Instant, endDate: Instant): Flow<BigDecimal> {
        return transactionsFlow.map { list ->
            list.filter { it.type == type && it.date >= startDate && it.date <= endDate }
                .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
        }
    }

    override fun getCategoryAmountsByType(type: TransactionType): Flow<List<CategoryAmount>> {
        return getTransactionsWithCategoryFlow().map { list ->
            list.filter { it.transaction.type == type }
                .groupBy { it.category.name }
                .map { (catName, txList) ->
                    val total = txList.fold(BigDecimal.ZERO) { acc, item -> acc + item.transaction.amount }
                    CategoryAmount(catName, total)
                }
        }
    }

    override fun getMonthlyTotalsFromDate(startDate: Instant): Flow<List<com.example.financeapp.data.RawMonthlyTotal>> {
        return transactionsFlow.map { emptyList() }
    }
}

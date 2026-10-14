package com.example.financeapp.data.transactions

import com.example.financeapp.data.CategoryAmount
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import kotlin.time.Instant

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: Int): Transaction?
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransactionById(id: Int)
    suspend fun getTransactionsByDateRange(startDate: Instant, endDate: Instant): List<Transaction>

    suspend fun getTransactionsByType(type: TransactionType): List<Transaction>
    suspend fun getTransactionsByCategory(categoryId: Int): List<Transaction>
    suspend fun getTransactionsByDatesAndCategory(startDate: Instant, endDate: Instant, categoryId: Int): List<Transaction>
    fun getTransactionsWithCategoryFlow(): Flow<List<TransactionWithCategory>>
    fun getRecentTransactionsWithCategoryFlow(limit: Int): Flow<List<TransactionWithCategory>>

    suspend fun getTransactionsWithCategory(): List<TransactionWithCategory>
    fun getFilteredTransactionsWithCategory(
        hasStartDate: Boolean,
        startDate: Instant,
        hasEndDate: Boolean,
        endDate: Instant,
        isFilterByCategory: Boolean,
        categoryId: Int,
        isFilterByType: Boolean,
        filterType: TransactionType
    ): Flow<List<TransactionWithCategory>>
    suspend fun updateTransactionAmount(id: Int, amount: BigDecimal)
    suspend fun updateTransactionCategory(id: Int, categoryId: Int)
    suspend fun updateTransactionType(id: Int, type: TransactionType)
    fun getCurrentMonthTotalAmount(): Flow<BigDecimal>
    fun getTotalAmountByType(type: TransactionType): Flow<BigDecimal>
    fun getTotalAmountByTypeAndDateRange(type: TransactionType, startDate: Instant, endDate: Instant): Flow<BigDecimal>
    fun getCategoryAmountsByType(type: TransactionType): Flow<List<CategoryAmount>>
}
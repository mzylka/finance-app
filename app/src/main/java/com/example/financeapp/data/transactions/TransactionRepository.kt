package com.example.financeapp.data.transactions

import com.example.financeapp.data.TransactionType
import kotlinx.coroutines.flow.Flow
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
    suspend fun updateTransactionAmount(id: Int, amount: Double)
    suspend fun updateTransactionCategory(id: Int, categoryId: Int)
    suspend fun updateTransactionType(id: Int, type: TransactionType)
}
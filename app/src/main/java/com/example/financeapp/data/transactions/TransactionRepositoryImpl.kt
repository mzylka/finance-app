package com.example.financeapp.data.transactions

import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.time.Instant

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDAO
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getTransactionsStream()
    }

    override suspend fun getTransactionById(id: Int): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    override suspend fun deleteTransactionById(id: Int) {
        transactionDao.deleteTransactionById(id)
    }

    override suspend fun getTransactionsByDateRange(
        startDate: kotlin.time.Instant,
        endDate: kotlin.time.Instant
    ): List<Transaction> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    override suspend fun getTransactionsByType(type: TransactionType): List<Transaction> {
        return transactionDao.getTransactionsByType(type)
    }

    override suspend fun getTransactionsByCategory(categoryId: Int): List<Transaction> {
        return transactionDao.getTransactionsByCategory(categoryId)
    }

    override suspend fun getTransactionsByDatesAndCategory(
        startDate: Instant,
        endDate: Instant,
        categoryId: Int
    ): List<Transaction> {
        return transactionDao.getTransactionsByDatesAndCategory(startDate, endDate, categoryId)
    }

    override fun getTransactionsWithCategory(): Flow<List<TransactionWithCategory>> {
        return transactionDao.getTransactionsWithCategory()
    }

    override fun getFilteredTransactionsWithCategory(
        isFilterByDate: Boolean,
        startDate: Instant,
        endDate: Instant,
        isFilterByCategory: Boolean,
        categoryId: Int,
        isFilterByType: Boolean,
        filterType: TransactionType
    ): Flow<List<TransactionWithCategory>> {
        return transactionDao.getFilteredTransactionsWithCategory(
            isFilterByDate, startDate, endDate, isFilterByCategory, categoryId, isFilterByType, filterType
        )
    }

    override suspend fun updateTransactionAmount(id: Int, amount: Double) {
        return transactionDao.updateTransactionAmount(id, amount)
    }

    override suspend fun updateTransactionCategory(id: Int, categoryId: Int) {
        return transactionDao.updateTransactionCategory(id, categoryId)
    }

    override suspend fun updateTransactionType(
        id: Int,
        type: TransactionType
    ) {
        return transactionDao.updateTransactionType(id, type)
    }
}

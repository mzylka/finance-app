package com.example.financeapp.data.transactions

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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
}

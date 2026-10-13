package com.example.financeapp.data.transactions

import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
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
        hasStartDate: Boolean,
        startDate: Instant,
        hasEndDate: Boolean,
        endDate: Instant,
        isFilterByCategory: Boolean,
        categoryId: Int,
        isFilterByType: Boolean,
        filterType: TransactionType
    ): Flow<List<TransactionWithCategory>> {
        return transactionDao.getFilteredTransactionsWithCategory(
            hasStartDate, startDate, hasEndDate, endDate, isFilterByCategory, categoryId, isFilterByType, filterType
        )
    }

    override suspend fun updateTransactionAmount(id: Int, amount: BigDecimal) {
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

    override fun getCurrentMonthTotalAmount(): Flow<BigDecimal> {
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
        return transactionDao.getTransactionsInDateRangeStream(startOfMonth, endOfMonth)
            .map { transactions ->
                transactions.fold(BigDecimal.ZERO) { acc, transaction ->
                    acc + transaction.signedAmount
                }
            }
    }
}

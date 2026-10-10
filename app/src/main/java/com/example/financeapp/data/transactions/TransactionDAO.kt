package com.example.financeapp.data.transactions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction as RoomTransaction
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
interface TransactionDAO {
    @Insert
    suspend fun insert(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionsStream(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): Transaction?

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTransactionsByDateRange(startDate: Instant, endDate: Instant): List<Transaction>

    @Query("SELECT * FROM transactions WHERE type = :type")
    suspend fun getTransactionsByType(type: TransactionType): List<Transaction>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId")
    suspend fun getTransactionsByCategory(categoryId: Int): List<Transaction>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate AND categoryId = :categoryId")
    suspend fun getTransactionsByDatesAndCategory(startDate: Instant, endDate: Instant, categoryId: Int): List<Transaction>

    @RoomTransaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionsWithCategory(): Flow<List<TransactionWithCategory>>

    @RoomTransaction
    @Query("""
        SELECT * FROM transactions 
        WHERE (:isFilterByDate = 0 OR date BETWEEN :startDate AND :endDate)
          AND (:isFilterByCategory = 0 OR categoryId = :categoryId)
          AND (:isFilterByType = 0 OR type = :filterType)
        ORDER BY date DESC
    """)
    fun getFilteredTransactionsWithCategory(
        isFilterByDate: Boolean,
        startDate: Instant,
        endDate: Instant,
        isFilterByCategory: Boolean,
        categoryId: Int,
        isFilterByType: Boolean,
        filterType: TransactionType
    ): Flow<List<TransactionWithCategory>>

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)

    @Query("UPDATE transactions SET amount = :amount WHERE id = :id")
    suspend fun updateTransactionAmount(id: Int, amount: Double)

    @Query("UPDATE transactions SET categoryId = :categoryId WHERE id = :id")
    suspend fun updateTransactionCategory(id: Int, categoryId: Int)

    @Query("UPDATE transactions SET type = :type WHERE id = :id")
    suspend fun updateTransactionType(id: Int, type: TransactionType)

    @Query("UPDATE transactions SET date = :date WHERE id = :id")
    suspend fun updateTransactionDate(id: Int, date: Instant)




}

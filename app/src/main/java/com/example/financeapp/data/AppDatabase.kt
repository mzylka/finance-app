package com.example.financeapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financeapp.data.budget.Budget
import com.example.financeapp.data.budget.BudgetDAO
import com.example.financeapp.data.categories.Category
import com.example.financeapp.data.categories.CategoryDAO
import com.example.financeapp.data.transactions.Transaction
import com.example.financeapp.data.transactions.TransactionDAO

@Database(
    entities = [Transaction::class, Category::class, Budget::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDAO
    abstract fun categoryDao(): CategoryDAO
    abstract fun budgetDao(): BudgetDAO
}
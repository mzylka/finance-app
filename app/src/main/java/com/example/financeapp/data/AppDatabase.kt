package com.example.financeapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.financeapp.data.transactions.Transaction
import com.example.financeapp.data.transactions.TransactionDAO

@Database(entities = [Transaction::class], version = 1)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDAO
}
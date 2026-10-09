package com.example.financeapp.data.budget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BudgetDAO {
    @Query("SELECT * FROM monthlyBudget WHERE id = 1 LIMIT 1")
    suspend fun getBudget() : Budget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)
}
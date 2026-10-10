package com.example.financeapp.data.budget

import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudget(): Flow<Budget>
    suspend fun insertBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
}
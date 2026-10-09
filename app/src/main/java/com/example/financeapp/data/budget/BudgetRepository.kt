package com.example.financeapp.data.budget

interface BudgetRepository {
    suspend fun getBudget(): Budget?
    suspend fun insertBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
}
package com.example.financeapp.data.budget
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDAO
) : BudgetRepository {
    override suspend fun getBudget(): Budget? {
        return budgetDao.getBudget()
    }

    override suspend fun insertBudget(budget: Budget) {
        return budgetDao.insert(budget)
    }

    override suspend fun updateBudget(budget: Budget) {
        return budgetDao.update(budget)
    }

}
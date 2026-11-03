package com.example.financeapp.ui.statistic

import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.transactions.Transaction
import com.example.financeapp.fakes.FakeBudgetRepository
import com.example.financeapp.fakes.FakeTransactionRepository
import com.example.financeapp.fakes.FakeUserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var transactionRepo: FakeTransactionRepository
    private lateinit var budgetRepo: FakeBudgetRepository
    private lateinit var preferencesRepo: FakeUserPreferencesRepository
    private lateinit var viewModel: StatisticsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        transactionRepo = FakeTransactionRepository()
        budgetRepo = FakeBudgetRepository()
        preferencesRepo = FakeUserPreferencesRepository()
        viewModel = StatisticsViewModel(transactionRepo, budgetRepo, preferencesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testStatistics_calculatesIncomeExpenseAndCategoryTotals() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        // Insert Income 1000
        transactionRepo.insertTransaction(
            Transaction(
                id = 0,
                date = Clock.System.now(),
                categoryId = 3, // Salary
                amount = BigDecimal("1000.00"),
                type = TransactionType.INCOME
            )
        )
        // Insert Expense 150
        transactionRepo.insertTransaction(
            Transaction(
                id = 0,
                date = Clock.System.now(),
                categoryId = 1, // Food & Dining
                amount = BigDecimal("150.00"),
                type = TransactionType.EXPENSE
            )
        )

        val state = viewModel.uiState.value
        assertEquals(BigDecimal("1000.00"), state.totalIncome)
        assertEquals(BigDecimal("150.00"), state.totalExpense)
        assertEquals(BigDecimal("1000.00"), state.monthlyBudget)
    }
}

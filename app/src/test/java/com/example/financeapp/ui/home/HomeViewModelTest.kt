package com.example.financeapp.ui.home

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
class HomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var transactionRepo: FakeTransactionRepository
    private lateinit var budgetRepo: FakeBudgetRepository
    private lateinit var preferencesRepo: FakeUserPreferencesRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        transactionRepo = FakeTransactionRepository()
        budgetRepo = FakeBudgetRepository()
        preferencesRepo = FakeUserPreferencesRepository()
        viewModel = HomeViewModel(transactionRepo, budgetRepo, preferencesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_loadsBudgetAndLimitsRecentTransactionsToFive() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        // Insert 7 transactions
        for (i in 1..7) {
            transactionRepo.insertTransaction(
                Transaction(
                    id = 0,
                    date = Clock.System.now(),
                    categoryId = 1,
                    amount = BigDecimal("10.00"),
                    type = TransactionType.EXPENSE
                )
            )
        }

        val state = viewModel.uiState.value
        assertEquals(BigDecimal("1000.00"), state.monthlyBudget)
        assertEquals(5, state.recentTransactions.size)
    }
}

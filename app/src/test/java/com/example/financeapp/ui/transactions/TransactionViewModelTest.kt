package com.example.financeapp.ui.transactions

import com.example.financeapp.data.TransactionType
import com.example.financeapp.fakes.FakeBudgetRepository
import com.example.financeapp.fakes.FakeCategoryRepository
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var transactionRepo: FakeTransactionRepository
    private lateinit var categoryRepo: FakeCategoryRepository
    private lateinit var budgetRepo: FakeBudgetRepository
    private lateinit var preferencesRepo: FakeUserPreferencesRepository
    private lateinit var viewModel: TransactionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        transactionRepo = FakeTransactionRepository()
        categoryRepo = FakeCategoryRepository()
        budgetRepo = FakeBudgetRepository()
        preferencesRepo = FakeUserPreferencesRepository()
        viewModel = TransactionViewModel(transactionRepo, categoryRepo, budgetRepo, preferencesRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_loadsCategoriesAndBudget() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        val state = viewModel.uiState.value
        assertEquals(3, state.categories.size)
        assertEquals(BigDecimal("1000.00"), state.budgetAmount)
    }

    @Test
    fun testAddTransaction_roundsAmountToTwoDecimalsAndInserts() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.addTransaction(
            amount = BigDecimal("12.3456"),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )

        val state = viewModel.uiState.value
        assertEquals(1, state.transactions.size)
        assertEquals(BigDecimal("12.35"), state.transactions[0].transaction.amount)
    }

    @Test
    fun testAddCategory_insertsNewCategory() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.addCategory("Utilities")

        val state = viewModel.uiState.value
        assertEquals(4, state.categories.size)
        assertEquals("Utilities", state.categories.last().name)
    }

    @Test
    fun testDeleteCategory_removesCategory() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.deleteCategory(1)

        val state = viewModel.uiState.value
        assertEquals(2, state.categories.size)
    }

    @Test
    fun testApplyFiltersAndClearFilters() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }

        viewModel.applyFilters(
            startDate = 1000L,
            endDate = 2000L,
            categoryId = 2,
            type = TransactionType.INCOME
        )

        var state = viewModel.uiState.value
        assertEquals(1000L, state.currentFilter.startDate)
        assertEquals(2000L, state.currentFilter.endDate)
        assertEquals(2, state.currentFilter.categoryId)
        assertEquals(TransactionType.INCOME, state.currentFilter.type)

        viewModel.clearFilters()

        state = viewModel.uiState.value
        assertNull(state.currentFilter.startDate)
        assertNull(state.currentFilter.endDate)
        assertNull(state.currentFilter.categoryId)
        assertNull(state.currentFilter.type)
    }
}

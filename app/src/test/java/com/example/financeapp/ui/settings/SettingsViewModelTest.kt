package com.example.financeapp.ui.settings

import com.example.financeapp.fakes.FakeBackupRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var preferencesRepo: FakeUserPreferencesRepository
    private lateinit var backupRepo: FakeBackupRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferencesRepo = FakeUserPreferencesRepository()
        backupRepo = FakeBackupRepository()
        viewModel = SettingsViewModel(preferencesRepo, backupRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testUpdateCurrency_updatesCurrencySymbol() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.selectedCurrency.collect()
        }

        assertEquals("$", viewModel.selectedCurrency.value)

        viewModel.updateCurrency("€")

        assertEquals("€", viewModel.selectedCurrency.value)
    }

    @Test
    fun testExportBackup_success() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.backupStatus.collect()
        }

        val outputStream = ByteArrayOutputStream()
        viewModel.exportBackup(outputStream)

        assertTrue(backupRepo.exportCalled)
        assertTrue(viewModel.backupStatus.value is BackupStatus.Success)
    }

    @Test
    fun testImportBackup_success() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.backupStatus.collect()
        }

        val inputStream = ByteArrayInputStream("{}".toByteArray())
        viewModel.importBackup(inputStream)

        assertTrue(backupRepo.importCalled)
        assertTrue(viewModel.backupStatus.value is BackupStatus.Success)
    }
}

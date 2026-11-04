package com.example.financeapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.backup.BackupRepository
import com.example.financeapp.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

sealed interface BackupStatus {
    object Idle : BackupStatus
    object Loading : BackupStatus
    data class Success(val message: String) : BackupStatus
    data class Error(val message: String) : BackupStatus
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    val selectedCurrency: StateFlow<String> = preferencesRepository.getCurrencySymbol()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "$"
        )

    private val _backupStatus = MutableStateFlow<BackupStatus>(BackupStatus.Idle)
    val backupStatus: StateFlow<BackupStatus> = _backupStatus.asStateFlow()

    fun updateCurrency(symbol: String) {
        viewModelScope.launch {
            preferencesRepository.setCurrencySymbol(symbol)
        }
    }

    fun exportBackup(outputStream: OutputStream) {
        viewModelScope.launch {
            _backupStatus.value = BackupStatus.Loading
            try {
                backupRepository.exportBackup(outputStream)
                _backupStatus.value = BackupStatus.Success("Backup exported successfully!")
            } catch (e: Exception) {
                _backupStatus.value = BackupStatus.Error("Export failed: ${e.localizedMessage}")
            }
        }
    }

    fun importBackup(inputStream: InputStream) {
        viewModelScope.launch {
            _backupStatus.value = BackupStatus.Loading
            try {
                backupRepository.importBackup(inputStream)
                _backupStatus.value = BackupStatus.Success("Backup restored successfully!")
            } catch (e: Exception) {
                _backupStatus.value = BackupStatus.Error("Import failed: ${e.localizedMessage}")
            }
        }
    }

    fun clearStatus() {
        _backupStatus.value = BackupStatus.Idle
    }
}

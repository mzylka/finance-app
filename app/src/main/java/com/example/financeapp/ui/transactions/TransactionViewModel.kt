package com.example.financeapp.ui.transactions

import androidx.lifecycle.ViewModel
import com.example.financeapp.data.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class TransactionState (
    val transactions: List<Transaction> = emptyList()
)
@HiltViewModel
class TransactionViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionState())
    val uiState: StateFlow<TransactionState> = _uiState.asStateFlow()

    fun getTransactions(): List<Transaction> {
        return _uiState.value.transactions;
    }
}

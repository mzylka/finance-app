package com.example.financeapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.TransactionT
import com.example.financeapp.data.transactions.Transaction
import com.example.financeapp.data.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionState (
    val transactions: List<Transaction> = emptyList()
)
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    val uiState: StateFlow<TransactionState> = transactionRepository.getAllTransactions()
        .map { transactions -> TransactionState(transactions = transactions) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionState()
        )

    // User Actions
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            transactionRepository.deleteTransactionById(id)
        }
    }
}

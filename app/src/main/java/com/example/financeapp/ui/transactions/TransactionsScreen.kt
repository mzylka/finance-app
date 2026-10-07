package com.example.financeapp.ui.transactions

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Transactions(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = viewModel()
) {
    LazyColumn(modifier = modifier) { }
}
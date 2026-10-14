package com.example.financeapp.ui.transactions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ExportButton(viewModel: TransactionViewModel) {
    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { fileUri ->
            context.contentResolver.openOutputStream(fileUri)?.let { outputStream ->
                viewModel.exportTransactionsToCsv(outputStream)
            }
        }
    }

    Button(
        onClick = {
            exportLauncher.launch("transactions_export.csv")
        }
    ) {
        Text("Export Transactions")
    }
}
package com.example.financeapp.ui.transactions

import android.R.attr.text
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.Transaction
import com.example.financeapp.data.TransactionCategory

@Composable
fun TransactionComponent(transaction: Transaction, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .border(1.dp, Color.Black, shape = CutCornerShape(1.dp))
    ) {
        Text(text = transaction.id.toString())
        Text(text = transaction.date.toString())
        Text(text = transaction.description)
        Text(
            text = transaction.category.toString(),
            color = if (transaction.category == TransactionCategory.INCOME) Color.Green else Color.Red
        )
        Text(text = transaction.amount.toString())
    }
}
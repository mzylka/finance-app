package com.example.financeapp.ui.transactions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.Transaction
import com.example.financeapp.data.TransactionCategory

@Composable
fun TransactionComponent(transaction: Transaction, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = transaction.date, modifier = Modifier.weight(1f))
        Text(text = transaction.description, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        if (transaction.category == TransactionCategory.INCOME) {
            Icon(
                Icons.Filled.ArrowUpward,
                contentDescription = transaction.category.toString(),
                tint = Color.Green,
                modifier = Modifier.weight(1f)
            )
        }
        else {
            Icon(
                Icons.Filled.ArrowDownward,
                contentDescription = transaction.category.toString(),
                tint = Color.Red,
                modifier = Modifier.weight(1f)
            )
        }
        Text(text = transaction.amount.toString(), modifier = Modifier.weight(1f))
    }
}
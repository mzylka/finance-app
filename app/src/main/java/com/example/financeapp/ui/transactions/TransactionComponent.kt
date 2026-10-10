package com.example.financeapp.ui.transactions

import android.R.attr.type
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.toJavaInstant

@Composable
fun TransactionComponent(transactionWithCategory: TransactionWithCategory, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())
        Text(
            text = formatter.format(transactionWithCategory.transaction.date.toJavaInstant()),
            modifier = Modifier.weight(1f),
            minLines = 2,
        )
        Text(text = transactionWithCategory.category.name, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        if (transactionWithCategory.transaction.type == TransactionType.INCOME) {
            Icon(
                Icons.Filled.ArrowUpward,
                contentDescription = transactionWithCategory.transaction.type.toString(),
                tint = Color.Green,
                modifier = Modifier.weight(1f)
            )
        }
        else {
            Icon(
                Icons.Filled.ArrowDownward,
                contentDescription = transactionWithCategory.transaction.type.toString(),
                tint = Color.Red,
                modifier = Modifier.weight(1f)
            )
        }
        Text(text = transactionWithCategory.transaction.amount.toString(), modifier = Modifier.weight(1f))
    }
}
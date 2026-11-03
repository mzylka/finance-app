package com.example.financeapp.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal

@Composable
fun MonthlyBudget(
    modifier: Modifier = Modifier,
    title: String,
    amount: BigDecimal,
    currency: String,
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = if (amount.longValueExact() == 0L) "N/A" else "${currency}${amount}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
package com.example.financeapp.ui.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.financeapp.ui.theme.FinanceAppTheme

@Composable
fun Statistics(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Statistics")
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPreview() {
    FinanceAppTheme {
        Statistics()
    }
}
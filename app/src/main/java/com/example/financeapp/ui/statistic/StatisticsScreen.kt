package com.example.financeapp.ui.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.financeapp.data.CategoryAmount
import com.example.financeapp.ui.theme.FinanceAppTheme
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.compose.HorizontalBarChart
import com.github.mikephil.charting.compose.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun Statistics(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Statistics Summary",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Total Income", fontSize = 14.sp)
                    Text(
                        text = "$${uiState.totalIncome}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Total Expense", fontSize = 14.sp)
                    Text(
                        text = "$${uiState.totalExpense}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Monthly Budget", fontSize = 14.sp)
                Text(
                    text = "$${uiState.monthlyBudget}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Month Expense vs Budget+Income",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                ExpenseVsBudgetPieChart(
                    currentMonthExpense = uiState.currentMonthExpense.toFloat(),
                    currentMonthIncome = uiState.currentMonthIncome.toFloat(),
                    monthlyBudget = uiState.monthlyBudget.toFloat(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height((120 + uiState.categoryExpens.size * 45).coerceIn(240, 420).dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Expenses by Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                ExpenseByCategoryBarChart(
                    categoryAmounts = uiState.categoryExpens,
                    isExpense = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height((120 + uiState.categoryExpens.size * 45).coerceIn(240, 420).dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Incomes by Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                ExpenseByCategoryBarChart(
                    categoryAmounts = uiState.categoryIncomes,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun ExpenseVsBudgetPieChart(
    currentMonthExpense: Float,
    currentMonthIncome: Float,
    monthlyBudget: Float,
    modifier: Modifier = Modifier
) {
    val remainingBudget = maxOf(0f, monthlyBudget + currentMonthIncome - currentMonthExpense)
    val entries = ArrayList<PieEntry<Any>>()
    if (currentMonthExpense > 0f) {
        entries.add(PieEntry(currentMonthExpense, "Spent ($${currentMonthExpense.toInt()})"))
    }
    if (remainingBudget > 0f) {
        entries.add(PieEntry(remainingBudget, "Remaining ($${remainingBudget.toInt()})"))
    }

    val dataSet = PieDataSet(entries, "").apply {
        colors = listOf(
            android.graphics.Color.rgb(239, 83, 80),
            android.graphics.Color.rgb(102, 187, 106)
        )
        valueTextSize = 13f
        valueTextColor = Color.White.toArgb()
    }
    val pieData = PieData(dataSet)

    PieChart(
        data = pieData,
        modifier = modifier,
        setup = {
            description.isEnabled = false
            isDrawHoleEnabled = false
            holeRadius = 45f
            transparentCircleRadius = 50f
            isUsePercentValuesEnabled = false
            legend.apply {
                isEnabled = true
                textSize = 12f
            }
        }
    )
}

@Composable
fun ExpenseByCategoryBarChart(
    modifier: Modifier = Modifier,
    categoryAmounts: List<CategoryAmount>,
    isExpense: Boolean = false,
) {
    val textColorOnSurface = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val type = if (isExpense) "Expense" else "Income"
    if (categoryAmounts.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No category ${type}s recorded")
        }
        return
    }

    val sortedList = categoryAmounts.reversed()
    val labels = sortedList.map { item -> item.categoryName }
    val entries = sortedList.mapIndexed { index, item ->
        BarEntry(index.toFloat(), item.totalAmount.toFloat())
    }

    val dataSet = BarDataSet(entries, "${type}s by Category").apply {
        color = if (isExpense) Color.Red.toArgb() else Color.Green.toArgb()
        valueTextSize = 12f
        valueTextColor = textColorOnSurface
    }
    val barData = BarData(dataSet).apply {
        barWidth = 0.5f
    }

    HorizontalBarChart(
        data = barData,
        modifier = modifier,
        setup = {
            description.isEnabled = false
            legend.isEnabled = false
            isFitBarsEnabled = true
            isDrawValueAboveBarEnabled = true

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                isDrawGridLinesEnabled = false
                granularity = 1f
                textSize = 11f
                textColor = textColorOnSurface
                valueFormatter = IndexAxisValueFormatter(labels)
            }

            axisLeft.apply {
                isDrawGridLinesEnabled = true
                axisMinimum = 0f
                textSize = 10f
                textColor = textColorOnSurface
            }

            axisRight.isEnabled = false
        }
    )
}

@Preview(showBackground = true)
@Composable
fun StatisticsPreview() {
    FinanceAppTheme {
        Statistics()
    }
}

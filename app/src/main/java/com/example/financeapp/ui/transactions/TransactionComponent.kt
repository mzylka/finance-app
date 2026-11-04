package com.example.financeapp.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.TransactionWithCategory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.toJavaInstant

@Composable
fun TransactionComponent(
    transactionWithCategory: TransactionWithCategory,
    modifier: Modifier = Modifier,
    onDeleteTransaction: ((Int) -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
            .clickable(enabled = onDeleteTransaction != null) { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())
            Text(
                text = formatter.format(transactionWithCategory.transaction.date.toJavaInstant()),
                modifier = Modifier.weight(1.5f),
                minLines = 2,
            )
            Text(
                text = transactionWithCategory.category.name,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )
            if (transactionWithCategory.transaction.type == TransactionType.INCOME) {
                Icon(
                    Icons.Filled.ArrowUpward,
                    contentDescription = transactionWithCategory.transaction.type.toString(),
                    tint = Color.Green,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Icon(
                    Icons.Filled.ArrowDownward,
                    contentDescription = transactionWithCategory.transaction.type.toString(),
                    tint = Color.Red,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = transactionWithCategory.transaction.amount.toString(),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
        }

        if (onDeleteTransaction != null) {
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                onDeleteTransaction(transactionWithCategory.transaction.id)
                                isExpanded = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Transaction",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.financeapp.data

import androidx.room.Embedded
import androidx.room.Relation
import com.example.financeapp.data.categories.Category
import com.example.financeapp.data.transactions.Transaction

data class CategoryWithTransactions(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val transactions: List<Transaction>
)
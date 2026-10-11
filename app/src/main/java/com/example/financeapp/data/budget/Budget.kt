package com.example.financeapp.data.budget

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "monthlyBudget")
data class Budget(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val amount: BigDecimal
)
package com.example.financeapp.data.backup

import com.example.financeapp.data.AppDatabase
import com.example.financeapp.data.TransactionType
import com.example.financeapp.data.budget.Budget
import com.example.financeapp.data.categories.Category
import com.example.financeapp.data.transactions.Transaction
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.OutputStream
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Instant

@Singleton
class BackupRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : BackupRepository {

    override suspend fun exportBackup(outputStream: OutputStream) {
        val categories = database.categoryDao().getAllCategories()
        val budget = database.budgetDao().getBudgetDirect()
        val transactions = database.transactionDao().getAllTransactions()

        val rootJson = JSONObject()
        rootJson.put("version", 1)

        val categoriesArray = JSONArray()
        categories.forEach { cat ->
            val catObj = JSONObject()
            catObj.put("id", cat.id)
            catObj.put("name", cat.name)
            categoriesArray.put(catObj)
        }
        rootJson.put("categories", categoriesArray)

        if (budget != null) {
            val budgetObj = JSONObject()
            budgetObj.put("id", budget.id)
            budgetObj.put("amount", budget.amount.toString())
            rootJson.put("budget", budgetObj)
        }

        val transactionsArray = JSONArray()
        transactions.forEach { tx ->
            val txObj = JSONObject()
            txObj.put("id", tx.id)
            txObj.put("date", tx.date.toEpochMilliseconds())
            txObj.put("categoryId", tx.categoryId)
            txObj.put("amount", tx.amount.toString())
            txObj.put("type", tx.type.name)
            transactionsArray.put(txObj)
        }
        rootJson.put("transactions", transactionsArray)

        outputStream.use { stream ->
            stream.write(rootJson.toString(2).toByteArray(Charsets.UTF_8))
        }
    }

    override suspend fun importBackup(inputStream: InputStream) {
        val jsonString = inputStream.use { stream ->
            stream.bufferedReader(Charsets.UTF_8).readText()
        }

        val rootJson = JSONObject(jsonString)

        // Restore Categories
        if (rootJson.has("categories")) {
            val categoriesArray = rootJson.getJSONArray("categories")
            for (i in 0 until categoriesArray.length()) {
                val catObj = categoriesArray.getJSONObject(i)
                val category = Category(
                    id = catObj.getInt("id"),
                    name = catObj.getString("name")
                )
                database.categoryDao().insert(category)
            }
        }

        // Restore Budget
        if (rootJson.has("budget")) {
            val budgetObj = rootJson.getJSONObject("budget")
            val amount = budgetObj.optString("amount", "0").toBigDecimalOrNull() ?: BigDecimal.ZERO
            val budget = Budget(
                id = budgetObj.optInt("id", 1),
                amount = amount
            )
            database.budgetDao().insert(budget)
        }

        // Restore Transactions
        if (rootJson.has("transactions")) {
            val transactionsArray = rootJson.getJSONArray("transactions")
            for (i in 0 until transactionsArray.length()) {
                val txObj = transactionsArray.getJSONObject(i)
                val typeName = txObj.optString("type", "EXPENSE")
                val type = runCatching { TransactionType.valueOf(typeName) }.getOrDefault(TransactionType.EXPENSE)
                val amount = txObj.optString("amount", "0").toBigDecimalOrNull() ?: BigDecimal.ZERO
                val transaction = Transaction(
                    id = txObj.getInt("id"),
                    date = Instant.fromEpochMilliseconds(txObj.getLong("date")),
                    categoryId = txObj.getInt("categoryId"),
                    amount = amount,
                    type = type
                )
                database.transactionDao().insert(transaction)
            }
        }
    }
}

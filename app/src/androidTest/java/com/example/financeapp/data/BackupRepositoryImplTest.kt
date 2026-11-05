package com.example.financeapp.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.financeapp.data.backup.BackupRepositoryImpl
import com.example.financeapp.data.budget.Budget
import com.example.financeapp.data.categories.Category
import com.example.financeapp.data.transactions.Transaction
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import kotlin.time.Instant

@RunWith(AndroidJUnit4::class)
class BackupRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: BackupRepositoryImpl

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = BackupRepositoryImpl(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testExportBackup_exportsJsonWithCategoriesBudgetAndTransactions(): Unit = runBlocking {
        database.categoryDao().insert(Category(id = 1, name = "Groceries"))
        database.budgetDao().insert(Budget(id = 1, amount = BigDecimal("500.00")))
        database.transactionDao().insert(
            Transaction(
                id = 1,
                date = Instant.fromEpochMilliseconds(1700000000000L),
                categoryId = 1,
                amount = BigDecimal("50.00"),
                type = TransactionType.EXPENSE
            )
        )

        val outputStream = ByteArrayOutputStream()
        repository.exportBackup(outputStream)

        val jsonString = outputStream.toString(Charsets.UTF_8.name())
        val rootObj = JSONObject(jsonString)

        assertEquals(1, rootObj.getInt("version"))
        assertTrue(rootObj.has("categories"))
        assertTrue(rootObj.has("budget"))
        assertTrue(rootObj.has("transactions"))

        val categoriesArr = rootObj.getJSONArray("categories")
        assertEquals(1, categoriesArr.length())
        assertEquals("Groceries", categoriesArr.getJSONObject(0).getString("name"))

        val budgetObj = rootObj.getJSONObject("budget")
        assertEquals("500.00", budgetObj.getString("amount"))

        val transactionsArr = rootObj.getJSONArray("transactions")
        assertEquals(1, transactionsArr.length())
        assertEquals("50.00", transactionsArr.getJSONObject(0).getString("amount"))
    }

    @Test
    fun testImportBackup_importsCategoriesBudgetAndTransactions(): Unit = runBlocking {
        val jsonPayload = """
            {
              "version": 1,
              "categories": [
                { "id": 10, "name": "Travel" }
              ],
              "budget": {
                "id": 1,
                "amount": "1200.00"
              },
              "transactions": [
                {
                  "id": 100,
                  "date": 1700000000000,
                  "categoryId": 10,
                  "amount": "300.00",
                  "type": "EXPENSE"
                }
              ]
            }
        """.trimIndent()

        val inputStream = ByteArrayInputStream(jsonPayload.toByteArray(Charsets.UTF_8))
        repository.importBackup(inputStream)

        val category = database.categoryDao().getCategoryById(10)
        assertNotNull(category)
        assertEquals("Travel", category?.name)

        val budget = database.budgetDao().getBudgetDirect()
        assertNotNull(budget)
        assertEquals(BigDecimal("1200.00"), budget?.amount)

        val transaction = database.transactionDao().getTransactionById(100)
        assertNotNull(transaction)
        assertEquals(BigDecimal("300.00"), transaction?.amount)
        assertEquals(TransactionType.EXPENSE, transaction?.type)
    }

    @Test(expected = Exception::class)
    fun testImportBackup_malformedJson_throwsException(): Unit = runBlocking {
        val malformedJson = "{ invalid json payload syntax"
        val inputStream = ByteArrayInputStream(malformedJson.toByteArray(Charsets.UTF_8))
        repository.importBackup(inputStream)
    }
}

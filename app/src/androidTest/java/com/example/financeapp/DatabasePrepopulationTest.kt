package com.example.financeapp

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.financeapp.data.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class DatabasePrepopulationTest {

    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO categories (name) VALUES ('Food & Dining')")
                    db.execSQL("INSERT INTO categories (name) VALUES ('Transportation')")
                    db.execSQL("INSERT INTO categories (name) VALUES ('Shopping')")
                    db.execSQL("INSERT INTO categories (name) VALUES ('Entertainment')")
                    db.execSQL("INSERT INTO categories (name) VALUES ('Bills & Utilities')")
                    db.execSQL("INSERT INTO categories (name) VALUES ('Salary')")

                    db.execSQL("INSERT INTO monthlyBudget (id, amount) VALUES (1, 0)")

                    db.execSQL("INSERT INTO transactions (date, categoryId, amount, type) VALUES (12, 1, 10000, 'INCOME')")
                }
            })
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testCategoriesArePrepopulated(): Unit = runBlocking {
        val categories = database.categoryDao().getAllCategories()
        assertEquals(6, categories.size)
        assertEquals("Food & Dining", categories[0].name)
        assertEquals("Salary", categories[5].name)
    }

    @Test
    fun testBudgetIsPrepopulated(): Unit = runBlocking {
        val budget = database.budgetDao().getBudgetDirect()
        assertNotNull(budget)
        assertEquals(BigDecimal("0.00"), budget?.amount)
    }

    @Test
    fun testTransactionIsPrepopulated(): Unit = runBlocking {
        val transactions = database.transactionDao().getAllTransactions()
        assertEquals(1, transactions.size)
        assertEquals(BigDecimal("100.00"), transactions[0].amount)
    }
}

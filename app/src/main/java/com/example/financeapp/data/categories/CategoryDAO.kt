package com.example.financeapp.data.categories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.financeapp.data.CategoryWithTransactions
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO{
    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT * FROM categories")
    fun getCategoriesStream(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Int)

    @Query("UPDATE categories SET name = :name WHERE id = :id")
    suspend fun updateCategoryName(id: Int, name: String)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    @Transaction
    @Query("SELECT * FROM categories")
    suspend fun getCategoriesWithTransactions(): List<CategoryWithTransactions>
}
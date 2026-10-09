package com.example.financeapp.data.categories

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: Int): Category?
    suspend fun insertCategory(category: Category)
    suspend fun deleteCategoryById(id: Int)
    suspend fun updateCategoryName(id: Int, name: String)
}
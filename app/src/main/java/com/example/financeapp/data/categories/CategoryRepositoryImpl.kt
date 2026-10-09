package com.example.financeapp.data.categories

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDAO
) : CategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getCategoriesStream()
    }

    override suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)
    }

    override suspend fun insertCategory(category: Category) {
        return categoryDao.insert(category)
    }

    override suspend fun deleteCategoryById(id: Int) {
        return categoryDao.deleteCategoryById(id)
    }

    override suspend fun updateCategoryName(id: Int, name: String) {
        return categoryDao.updateCategoryName(id, name)
    }

}
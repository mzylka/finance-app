package com.example.financeapp.di

import android.content.Context
import androidx.room.Room
import com.example.financeapp.data.AppDatabase
import com.example.financeapp.data.budget.BudgetDAO
import com.example.financeapp.data.budget.BudgetRepository
import com.example.financeapp.data.budget.BudgetRepositoryImpl
import com.example.financeapp.data.categories.CategoryDAO
import com.example.financeapp.data.categories.CategoryRepository
import com.example.financeapp.data.categories.CategoryRepositoryImpl
import com.example.financeapp.data.transactions.TransactionRepositoryImpl
import com.example.financeapp.data.transactions.TransactionDAO
import com.example.financeapp.data.transactions.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        impl: BudgetRepositoryImpl
    ): BudgetRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finance_app_db"
        ).build()
    }

    @Provides
    fun provideTransactionDao(
        database: AppDatabase
    ): TransactionDAO {
        return database.transactionDao()
    }

    @Provides
    fun provideCategoryDao(
        database: AppDatabase
    ): CategoryDAO {
        return database.categoryDao()
    }

    @Provides
    fun provideBudgetDao(
        database: AppDatabase
    ): BudgetDAO {
        return database.budgetDao()
    }
}

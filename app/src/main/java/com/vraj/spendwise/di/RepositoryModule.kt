package com.vraj.spendwise.di

import com.vraj.spendwise.data.local.repository.ExpenseRepository
import com.vraj.spendwise.data.local.repository.ExpenseRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindExpenseRepository(expenseRepository: ExpenseRepositoryImpl): ExpenseRepository
}
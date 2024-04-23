package com.vraj.spendwise.data.local.repository

import com.vraj.spendwise.data.local.dao.ExpenseDao
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import javax.inject.Inject

interface ExpenseRepository {
    suspend fun addExpense(expenseEntity: ExpenseEntity)

    suspend fun getExpense(name: String): ExpenseEntity?

    suspend fun updateExpense(name: String, amount: Double)
}

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(expenseEntity: ExpenseEntity) {
        expenseDao.addExpense(expenseEntity)
    }

    override suspend fun getExpense(name: String): ExpenseEntity? {
        return expenseDao.getExpense(name)
    }

    override suspend fun updateExpense(name: String, amount: Double) {
        return expenseDao.updateExpense(name, amount)
    }
}
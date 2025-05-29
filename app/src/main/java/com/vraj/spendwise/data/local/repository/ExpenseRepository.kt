package com.vraj.spendwise.data.local.repository

import com.vraj.spendwise.data.local.dao.ExpenseDao
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ExpenseRepository {
    suspend fun addExpense(name: String, amount: Double)

    suspend fun editExpense(id: Int, name: String, amount: Double)

    suspend fun getLastExpense(): ExpenseEntity?

    suspend fun getRecentExpenses(limit: Int, offset: Int): List<ExpenseEntity>

    suspend fun totalCount(): Flow<Int>

    suspend fun removeExpense(id: Int)

    suspend fun getDistinctMonthsAndYears(): List<ExpenseEntity.MonthAndYear>

    suspend fun getDataForMonthAndYear(month: String, year: String): List<ExpenseEntity>

    suspend fun getAllExpenses(): List<ExpenseEntity>

    suspend fun getAllExpensesName(): List<String>
}

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override suspend fun addExpense(name: String, amount: Double) =
        expenseDao.addExpense(ExpenseEntity(name = name, amount = amount))

    override suspend fun editExpense(id: Int, name: String, amount: Double) =
        expenseDao.editExpense(id, name, amount)

    override suspend fun getLastExpense(): ExpenseEntity? =
        expenseDao.getLastExpense()

    override suspend fun getRecentExpenses(limit: Int, offset: Int): List<ExpenseEntity> =
        expenseDao.getRecentExpenses(limit, offset)

    override suspend fun totalCount(): Flow<Int> =
        expenseDao.totalCount()

    override suspend fun removeExpense(id: Int) =
        expenseDao.removeExpense(id)

    override suspend fun getDistinctMonthsAndYears(): List<ExpenseEntity.MonthAndYear> =
        expenseDao.getDistinctMonthsAndYears()

    override suspend fun getDataForMonthAndYear(month: String, year: String): List<ExpenseEntity> =
        expenseDao.getDataForMonthAndYear(month, year)

    override suspend fun getAllExpenses(): List<ExpenseEntity> =
        expenseDao.getAllExpenses()

    override suspend fun getAllExpensesName(): List<String> =
        expenseDao.getAllExpansesName()
}
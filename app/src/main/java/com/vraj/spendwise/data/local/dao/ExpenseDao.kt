package com.vraj.spendwise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addExpense(expenseEntity: ExpenseEntity)

    @Query("SELECT * FROM UserExpense ORDER BY id DESC LIMIT 1")
    suspend fun getLastExpense(): ExpenseEntity?

    @Query("SELECT * FROM UserExpense ORDER BY id DESC LIMIT :limit OFFSET :offset")
    suspend fun getRecentExpenses(limit: Int, offset: Int): List<ExpenseEntity>

    @Query("SELECT COUNT(*) FROM UserExpense")
    fun totalCount(): Flow<Int>

    @Query("DELETE FROM UserExpense WHERE ID = :id")
    suspend fun removeExpense(id: Int)
}
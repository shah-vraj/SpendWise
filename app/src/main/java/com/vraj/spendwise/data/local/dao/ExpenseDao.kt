package com.vraj.spendwise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vraj.spendwise.data.local.entity.ExpenseEntity

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addExpense(expenseEntity: ExpenseEntity)

    @Query("SELECT * FROM UserExpense WHERE name = :name")
    suspend fun getExpense(name: String): ExpenseEntity?

    @Query("UPDATE UserExpense SET amount = :amount WHERE name = :name")
    suspend fun updateExpense(name: String, amount: Double)
}
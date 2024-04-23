package com.vraj.spendwise.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "UserExpense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,

    @ColumnInfo("date")
    val date: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("amount")
    val amount: Double
)
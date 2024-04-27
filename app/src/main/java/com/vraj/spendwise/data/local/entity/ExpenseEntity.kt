package com.vraj.spendwise.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vraj.spendwise.util.MonthOfYear
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
) {
    data class MonthAndYear(val month: String, val year: String) {
        val fullString: String
            get() = MonthOfYear.getMonthStringFromNumericString(month) + " " + year
    }

    val createdDateFormatted : String
        get() = date.format(DateTimeFormatter.ofPattern("dd LLLL yyyy"))
}
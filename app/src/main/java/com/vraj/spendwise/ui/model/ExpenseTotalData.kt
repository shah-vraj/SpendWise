package com.vraj.spendwise.ui.model

import com.vraj.spendwise.data.local.entity.ExpenseEntity

data class ExpenseTotalData(
    val name: String,
    val amount: Double,
    val relatedExpenses: List<ExpenseEntity>,
    val isExpanded: Boolean = false
)

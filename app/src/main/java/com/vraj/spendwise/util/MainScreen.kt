package com.vraj.spendwise.util

sealed class MainScreen(val route: String) {
    data object InputExpenseScreen : MainScreen("InputExpenseScreen")
    data object TotalExpensesScreen : MainScreen("TotalExpensesScreen")
}
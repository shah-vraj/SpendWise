package com.vraj.spendwise.viewmodel

import androidx.lifecycle.viewModelScope
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.data.local.repository.ExpenseRepository
import com.vraj.spendwise.di.IoDispatcher
import com.vraj.spendwise.ui.base.BaseViewModel
import com.vraj.spendwise.ui.model.ExpenseTotalData
import com.vraj.spendwise.util.MonthOfYear
import com.vraj.spendwise.util.extension.toStringByLimitingDecimalDigits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TotalExpenseViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val expenseRepository: ExpenseRepository
) : BaseViewModel() {

    private val _showMonthFilterBottomSheet = MutableStateFlow(false)
    val showMonthFilterBottomSheet = _showMonthFilterBottomSheet.asStateFlow()

    private val _monthAndYears = MutableStateFlow<List<String>>(emptyList())
    val monthAndYears = _monthAndYears.asStateFlow()

    private val _selectedMonthAndYear = MutableStateFlow("")
    val selectedMonthAndYear = _selectedMonthAndYear.asStateFlow()

    private val _filteredExpenses = MutableStateFlow<List<ExpenseTotalData>>(emptyList())
    val filteredExpenses = _filteredExpenses.asStateFlow()

    private val currentMonthAndYearString: String
        get() = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
            .format(Calendar.getInstance().time)

    val overallTotal = filteredExpenses.map { expenses ->
        expenses.sumOf { it.amount }
            .toStringByLimitingDecimalDigits(3)
    }

    init {
        loadMonthAndYears()
        setCurrentMonthAndYearAsSelected()
    }

    fun showMonthFilterBottomSheet(shouldShow: Boolean) {
        _showMonthFilterBottomSheet.value = shouldShow
    }

    fun setCurrentMonthAndYearAsSelected() {
        setSelectedMonthAndYear(currentMonthAndYearString)
    }

    fun setSelectedMonthAndYear(selectedMonthAndYear: String) {
        _selectedMonthAndYear.value = selectedMonthAndYear
        updateFilteredExpenses(selectedMonthAndYear)
    }

    fun setFilteredExpenseExpanded(isExpanded: Boolean, name: String) {
        _filteredExpenses.value = _filteredExpenses.value.map {
            it.copy(isExpanded = if (it.name == name) isExpanded else it.isExpanded)
        }
    }

    private fun loadMonthAndYears() {
        viewModelScope.launch(ioDispatcher) {
            _monthAndYears.value = expenseRepository
                .getDistinctMonthsAndYears()
                .map { it.fullString }
                .let {
                    buildList {
                        addAll(it)
                        if (!it.contains(currentMonthAndYearString))
                            add(currentMonthAndYearString)
                        add(ALL_TIME_EXPENSES)
                    }
                }
        }
    }

    private fun updateFilteredExpenses(monthAndYearString: String) {
        viewModelScope.launch(ioDispatcher) {
            _filteredExpenses.value = when (monthAndYearString) {
                ALL_TIME_EXPENSES -> expenseRepository.getAllExpenses()
                else -> {
                    val (month, year) = monthAndYearString.split("\\s+".toRegex())
                    val numericMonth = MonthOfYear.getNumericStringFromMonthString(month)
                    expenseRepository.getDataForMonthAndYear(numericMonth, year)
                }
            }.groupByExpenses().sortedByDescending { it.amount }
        }
    }

    private fun List<ExpenseEntity>.groupByExpenses(): List<ExpenseTotalData> = buildList {
        this@groupByExpenses.groupBy(ExpenseEntity::name)
            .mapValues {
                add(
                    ExpenseTotalData(
                        name = it.key,
                        amount = it.value.sumOf { entity -> entity.amount },
                        relatedExpenses = it.value
                    )
                )
            }
    }

    companion object {
        private const val ALL_TIME_EXPENSES = "All time"
    }
}
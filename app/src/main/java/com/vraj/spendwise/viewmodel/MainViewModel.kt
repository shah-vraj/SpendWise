package com.vraj.spendwise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.data.local.repository.ExpenseRepository
import com.vraj.spendwise.di.IoDispatcher
import com.vraj.spendwise.util.AppToast
import com.vraj.spendwise.util.MonthOfYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private var currentOffset = 0

    private val _expenseType = MutableStateFlow("")
    val expenseType = _expenseType.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount = _amount.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses = _expenses.asStateFlow()

    private val _hasMoreExpenseToLoad = MutableStateFlow(true)
    val hasMoreExpenseToLoad = _hasMoreExpenseToLoad.asStateFlow()

    private val _showToast = MutableStateFlow<AppToast>(AppToast.Nothing)
    val showToast = _showToast.asStateFlow()

    private val _expenseBottomSheetState = MutableStateFlow<Pair<Boolean, ExpenseEntity?>>(false to null)
    val expenseBottomSheetState = _expenseBottomSheetState.asStateFlow()

    private val _showMonthFilterBottomSheet = MutableStateFlow(false)
    val showMonthFilterBottomSheet = _showMonthFilterBottomSheet.asStateFlow()

    private val _monthAndYears = MutableStateFlow<List<String>>(emptyList())
    val monthAndYears = _monthAndYears.asStateFlow()

    private val _selectedMonthAndYear = MutableStateFlow("")
    val selectedMonthAndYear = _selectedMonthAndYear.asStateFlow()

    private val _filteredExpenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val filteredExpenses = _filteredExpenses.asStateFlow()

    private val currentMonthAndYearString: String
        get() = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
            .format(Calendar.getInstance().time)

    init {
        loadRecentExpenses()
        loadMonthAndYears()
        setCurrentMonthAndYearAsSelected()
        viewModelScope.launch {
            expenses.combine(expenseRepository.totalCount()) { expenses, totalExpenses ->
                expenses.size != totalExpenses
            }.collectLatest {
                _hasMoreExpenseToLoad.value = it
            }
        }
    }

    fun setExpenseType(value: String) {
        _expenseType.value = value
    }

    fun setAmount(value: String) {
        _amount.value = value
    }

    fun validateInputAndAddToDatabase() {
        val expenseType = expenseType.value
        val amount = amount.value.toDoubleOrNull() ?: run {
            _showToast.value = AppToast.Error(R.string.invalid_input_error)
            return
        }

        if (expenseType.isBlank() || amount <= 0f) {
            _showToast.value = AppToast.Error(R.string.invalid_input_error)
            return
        }

        viewModelScope.launch(ioDispatcher) {
            addExpense(expenseType, amount)
            _showToast.value = AppToast.Success(R.string.add_expense_success)
        }
    }

    fun loadRecentExpenses() {
        viewModelScope.launch(ioDispatcher) {
            val newData = expenseRepository.getRecentExpenses(RECENT_EXPENSES_FETCH_LIMIT, currentOffset)
            currentOffset += newData.count()
            _expenses.value = buildList {
                addAll(expenses.value)
                addAll(newData)
            }
        }
    }

    fun showToast(appToast: AppToast) {
        _showToast.value = appToast
    }

    fun onToastShown() {
        _showToast.value = AppToast.Nothing
    }

    fun showExpenseBottomSheet(shouldShow: Boolean, expenseEntity: ExpenseEntity?) {
        _expenseBottomSheetState.value = shouldShow to expenseEntity
    }

    fun addExpense(expenseEntity: ExpenseEntity) {
        viewModelScope.launch(ioDispatcher) {
            addExpense(expenseEntity.name, expenseEntity.amount)
        }
    }

    fun removeExpense(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            expenseRepository.removeExpense(id)
            _expenses.value = buildList {
                addAll(expenses.value)
                removeIf { it.id == id }
            }
            currentOffset--
        }
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

    private suspend fun addExpense(name: String, amount: Double) {
        expenseRepository.addExpense(name = name, amount = amount)
        expenseRepository.getLastExpense()?.let {
            _expenses.value = buildList {
                add(it)
                addAll(expenses.value)
            }
        }
        currentOffset++
        _expenseType.value = ""
        _amount.value = ""
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

    private fun List<ExpenseEntity>.groupByExpenses(): List<ExpenseEntity> = buildList {
        this@groupByExpenses.groupBy(ExpenseEntity::name)
            .mapValues {
                add(
                    ExpenseEntity(
                        name = it.key,
                        amount = it.value
                            .map { entity -> entity.amount }
                            .sumOf { amount -> amount }
                    )
                )
            }
    }

    companion object {
        private const val RECENT_EXPENSES_FETCH_LIMIT = 10
        private const val ALL_TIME_EXPENSES = "All time"
        const val NUMBER_OF_ROWS_OF_RECENT_EXPENSES = 3
        const val SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES = 15
        const val RECENT_EXPENSE_SINGLE_ITEM_HEIGHT = 45
    }
}
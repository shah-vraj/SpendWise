package com.vraj.spendwise.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.data.local.repository.ExpenseRepository
import com.vraj.spendwise.di.IoDispatcher
import com.vraj.spendwise.ui.base.BaseViewModel
import com.vraj.spendwise.ui.model.AlertDialogData
import com.vraj.spendwise.ui.model.ExpenseTotalData
import com.vraj.spendwise.util.AppToast
import com.vraj.spendwise.util.MonthOfYear
import com.vraj.spendwise.util.extension.isLastCharValid
import com.vraj.spendwise.util.extension.toStringByLimitingDecimalDigits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val expenseRepository: ExpenseRepository
) : BaseViewModel() {

    private var currentOffset = 0

    private val _expenseType = MutableStateFlow(TextFieldValue())
    val expenseType = _expenseType.asStateFlow()

    private val _amount = MutableStateFlow(TextFieldValue())
    val amount = _amount.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses = _expenses.asStateFlow()

    private val _hasMoreExpenseToLoad = MutableStateFlow(true)
    val hasMoreExpenseToLoad = _hasMoreExpenseToLoad.asStateFlow()

    private val _showToast = MutableStateFlow<AppToast>(AppToast.Nothing)
    val showToast = _showToast.asStateFlow()

    private val _expenseBottomSheetState = MutableStateFlow(false)
    val expenseBottomSheetState = _expenseBottomSheetState.asStateFlow()

    private val _expenseBottomSheetEntity = MutableStateFlow<ExpenseEntity?>(null)
    val expenseBottomSheetEntity = _expenseBottomSheetEntity.asStateFlow()

    private val _showMonthFilterBottomSheet = MutableStateFlow(false)
    val showMonthFilterBottomSheet = _showMonthFilterBottomSheet.asStateFlow()

    private val _monthAndYears = MutableStateFlow<List<String>>(emptyList())
    val monthAndYears = _monthAndYears.asStateFlow()

    private val _selectedMonthAndYear = MutableStateFlow("")
    val selectedMonthAndYear = _selectedMonthAndYear.asStateFlow()

    private val _filteredExpenses = MutableStateFlow<List<ExpenseTotalData>>(emptyList())
    val filteredExpenses = _filteredExpenses.asStateFlow()

    private val _showAlertDialog = MutableStateFlow<AlertDialogData?>(null)
    val showAlertDialog = _showAlertDialog.asStateFlow()

    private val _expenseTypeDropdownItems = MutableStateFlow<List<String>>(emptyList())
    val expenseTypeDropdownItems = _expenseTypeDropdownItems.asStateFlow()

    private val _isDropdownExpanded = MutableStateFlow(false)
    val isDropdownExpanded = _isDropdownExpanded.asStateFlow()
    
    private val _isEntityEditInProgress = MutableStateFlow(false)
    val isEntityEditInProgress = _isEntityEditInProgress.asStateFlow()
    
    private var entityIdToEdit: Int = -1

    private val currentMonthAndYearString: String
        get() = SimpleDateFormat("LLLL yyyy", Locale.getDefault())
            .format(Calendar.getInstance().time)

    private val allExpensesNames = mutableListOf<String>()

    val overallTotal = filteredExpenses.map { expenses ->
        expenses.sumOf { it.amount }
            .toStringByLimitingDecimalDigits(3)
    }

    init {
        loadRecentExpenses()
        updateAllExpensesName()
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

    fun setExpenseType(value: TextFieldValue) {
        val text = value.text
        if (text.length > EXPENSE_NAME_CHAR_LIMIT || !text.isLastCharValid())
            return
        _expenseType.value = value
        _expenseTypeDropdownItems.value = allExpensesNames.filter {
            text.isNotEmpty() && it.lowercase().startsWith(text.lowercase()) && it != text
        }.take(MAX_NUMBER_OF_EXPENSE_TYPE_SUGGESTION)
        debounce(EXPENSE_TYPE_DROPDOWN_DEBOUNCE_TIME) {
            _isDropdownExpanded.value = _expenseTypeDropdownItems.value.isNotEmpty()
        }
    }

    fun setAmount(value: TextFieldValue) {
        if (value.text.length > EXPENSE_AMOUNT_CHAR_LIMIT)
            return
        _amount.value = value
    }

    fun validateInputAndAddToDatabase() {
        val expenseType = expenseType.value.text.trim()
        val amount = amount.value.text.toDoubleOrNull() ?: run {
            _showToast.value = AppToast.Error(R.string.invalid_input_error)
            return
        }

        if (expenseType.isBlank() || amount <= 0f) {
            _showToast.value = AppToast.Error(R.string.invalid_input_error)
            return
        }

        viewModelScope.launch(ioDispatcher) {
            if (isEntityEditInProgress.value) {
                editExpense(entityIdToEdit, expenseType, amount)
                _showToast.value = AppToast.Success(R.string.edit_expense_success)
                return@launch
            }
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

    fun setExpenseBottomSheetEntity(expenseEntity: ExpenseEntity?) {
        _expenseBottomSheetEntity.value = expenseEntity
    }

    fun setExpenseBottomSheetState(shouldShow: Boolean) {
        _expenseBottomSheetState.value = shouldShow
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
            updateAllExpensesName()
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

    fun showAlertDialog(alertDialogData: AlertDialogData?) {
        _showAlertDialog.value = alertDialogData
    }

    fun setDropdownExpanded(isExpanded: Boolean) {
        _isDropdownExpanded.value = isExpanded
    }

    fun edit(entity: ExpenseEntity) {
        _isEntityEditInProgress.value = true
        entityIdToEdit = entity.id
        _expenseType.value = TextFieldValue(entity.name)
        _amount.value = TextFieldValue(entity.amountString)
    }

    fun clearEditMode() {
        _isEntityEditInProgress.value = false
        entityIdToEdit = -1
        _expenseType.value = TextFieldValue()
        _amount.value = TextFieldValue()
    }

    fun setFilteredExpenseExpanded(isExpanded: Boolean, name: String) {
        _filteredExpenses.value = _filteredExpenses.value.map {
            it.copy(isExpanded = if (it.name == name) isExpanded else it.isExpanded)
        }
    }

    private suspend fun editExpense(id: Int, name: String, amount: Double) {
        expenseRepository.editExpense(id, name, amount)
        _expenses.value = _expenses.value.map {
            if (it.id == id) it.copy(name = name, amount = amount) else it
        }
        clearEditMode()
        updateAllExpensesName()
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
        _expenseType.value = TextFieldValue()
        _amount.value = TextFieldValue()
        updateAllExpensesName()
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

    private fun updateAllExpensesName() {
        viewModelScope.launch(ioDispatcher) {
            allExpensesNames.clear()
            allExpensesNames.addAll(expenseRepository.getAllExpensesName())
        }
    }

    companion object {
        private const val RECENT_EXPENSES_FETCH_LIMIT = 10
        private const val ALL_TIME_EXPENSES = "All time"
        private const val EXPENSE_NAME_CHAR_LIMIT = 25
        private const val EXPENSE_AMOUNT_CHAR_LIMIT = 8
        private const val MAX_NUMBER_OF_EXPENSE_TYPE_SUGGESTION = 3
        private const val EXPENSE_TYPE_DROPDOWN_DEBOUNCE_TIME = 300L
        const val NUMBER_OF_ROWS_OF_RECENT_EXPENSES = 3
        const val SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES = 15
        const val RECENT_EXPENSE_SINGLE_ITEM_HEIGHT = 45
    }
}
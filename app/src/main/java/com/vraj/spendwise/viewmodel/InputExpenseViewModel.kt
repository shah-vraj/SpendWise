package com.vraj.spendwise.viewmodel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.data.local.repository.ExpenseRepository
import com.vraj.spendwise.di.IoDispatcher
import com.vraj.spendwise.ui.base.BaseViewModel
import com.vraj.spendwise.util.AppToast
import com.vraj.spendwise.util.extension.isLastCharValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputExpenseViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val expenseRepository: ExpenseRepository
) : BaseViewModel() {

    private val _expenseType = MutableStateFlow(TextFieldValue())
    val expenseType = _expenseType.asStateFlow()

    private val _amount = MutableStateFlow(TextFieldValue())
    val amount = _amount.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses = _expenses.asStateFlow()

    private val _isEntityEditInProgress = MutableStateFlow(false)
    val isEntityEditInProgress = _isEntityEditInProgress.asStateFlow()

    private val _expenseTypeDropdownItems = MutableStateFlow<List<String>>(emptyList())
    val expenseTypeDropdownItems = _expenseTypeDropdownItems.asStateFlow()

    private val _isDropdownExpanded = MutableStateFlow(false)
    val isDropdownExpanded = _isDropdownExpanded.asStateFlow()

    private val _expenseBottomSheetState = MutableStateFlow(false)
    val expenseBottomSheetState = _expenseBottomSheetState.asStateFlow()

    private val _expenseBottomSheetEntity = MutableStateFlow<ExpenseEntity?>(null)
    val expenseBottomSheetEntity = _expenseBottomSheetEntity.asStateFlow()

    private val _hasMoreExpenseToLoad = MutableStateFlow(true)
    val hasMoreExpenseToLoad = _hasMoreExpenseToLoad.asStateFlow()

    private var currentOffset = 0
    private var entityIdToEdit: Int = -1
    private val allExpensesNames = mutableListOf<String>()

    init {
        loadRecentExpenses()
        updateAllExpensesName()
        viewModelScope.launch {
            expenses.combine(expenseRepository.totalCount()) { expenses, totalExpenses ->
                expenses.size != totalExpenses
            }.collectLatest {
                _hasMoreExpenseToLoad.value = it
            }
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

    fun setExpenseType(value: TextFieldValue) {
        val text = value.text
        if (text.length > EXPENSE_NAME_CHAR_LIMIT || !text.isLastCharValid())
            return
        _expenseType.value = value
        _expenseTypeDropdownItems.value = allExpensesNames
            .filter { it.similarTo(text) }
            .take(MAX_NUMBER_OF_EXPENSE_TYPE_SUGGESTION)
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

    fun setExpenseBottomSheetState(shouldShow: Boolean) {
        _expenseBottomSheetState.value = shouldShow
    }

    fun setExpenseBottomSheetEntity(expenseEntity: ExpenseEntity?) {
        _expenseBottomSheetEntity.value = expenseEntity
    }

    fun setDropdownExpanded(isExpanded: Boolean) {
        _isDropdownExpanded.value = isExpanded
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

    private suspend fun editExpense(id: Int, name: String, amount: Double) {
        expenseRepository.editExpense(id, name, amount)
        _expenses.value = _expenses.value.map {
            if (it.id == id) it.copy(name = name, amount = amount) else it
        }
        clearEditMode()
        updateAllExpensesName()
    }

    private fun updateAllExpensesName() {
        viewModelScope.launch(ioDispatcher) {
            allExpensesNames.clear()
            allExpensesNames.addAll(expenseRepository.getAllExpensesName())
        }
    }

    private fun String.similarTo(text: String): Boolean =
        text.isNotEmpty()
                && lowercase().startsWith(text.lowercase())
                && this != text

    companion object {
        private const val RECENT_EXPENSES_FETCH_LIMIT = 10
        private const val EXPENSE_NAME_CHAR_LIMIT = 25
        private const val EXPENSE_AMOUNT_CHAR_LIMIT = 8
        private const val MAX_NUMBER_OF_EXPENSE_TYPE_SUGGESTION = 3
        private const val EXPENSE_TYPE_DROPDOWN_DEBOUNCE_TIME = 300L
        const val NUMBER_OF_ROWS_OF_RECENT_EXPENSES = 3
        const val SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES = 15
        const val RECENT_EXPENSE_SINGLE_ITEM_HEIGHT = 45
    }
}
package com.vraj.spendwise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.data.local.repository.ExpenseRepository
import com.vraj.spendwise.di.IoDispatcher
import com.vraj.spendwise.util.AppToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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

    init {
        loadRecentExpenses()
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

    private suspend fun addExpense(name: String, amount: Double) {
        expenseRepository.addExpense(name = name, amount = amount)
        expenseRepository.getLastExpense()?.let {
            _expenses.value = buildList {
                add(it)
                addAll(expenses.value)
            }
        }
        currentOffset++
    }

    companion object {
        private const val RECENT_EXPENSES_FETCH_LIMIT = 10
        const val NUMBER_OF_ROWS_OF_RECENT_EXPENSES = 3
        const val SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES = 15
        const val RECENT_EXPENSE_SINGLE_ITEM_HEIGHT = 45
    }
}
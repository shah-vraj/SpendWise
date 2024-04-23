package com.vraj.spendwise.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.data.local.repository.ExpenseRepository
import com.vraj.spendwise.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _expenseType = MutableStateFlow("")
    val expenseType = _expenseType.asStateFlow()

    private val _amount = MutableStateFlow("")
    val amount = _amount.asStateFlow()

    lateinit var amountInDouble: StateFlow<Double?>

    init {
        viewModelScope.launch {
            amountInDouble = _amount
                .map { it.toDoubleOrNull() }
                .stateIn(viewModelScope)
        }
    }

    fun setExpenseType(value: String) {
        _expenseType.value = value
    }

    fun setAmount(value: String) {
        _amount.value = value
    }

    fun addExpense(expenseEntity: ExpenseEntity) {
        viewModelScope.launch(ioDispatcher) {
            expenseRepository.addExpense(expenseEntity)
        }
    }

    fun checkIfExpenseExists(name: String, completion: (ExpenseEntity?) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            completion(expenseRepository.getExpense(name))
        }
    }

    fun updateExpense(name: String, amount: Double) {
        viewModelScope.launch(ioDispatcher) {
            expenseRepository.updateExpense(name, amount)
        }
    }
}
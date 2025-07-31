package com.vraj.spendwise.viewmodel

import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.data.local.repository.ExpenseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MainViewModelTest {

    @Mock
    private lateinit var ioDispatcher: CoroutineDispatcher

    @Mock
    private lateinit var expenseRepository: ExpenseRepository

    private lateinit var viewModel: MainViewModel
    private val dummyExpenses = listOf(
        ExpenseEntity(id = 1, name = "A", amount = 1.0),
        ExpenseEntity(id = 2, name = "B", amount = 2.0),
        ExpenseEntity(id = 3, name = "C", amount = 3.0)
    )

    @BeforeEach
    fun setUp() {
        viewModel = MainViewModel(ioDispatcher, expenseRepository)
    }

    @Test
    fun shouldLoadRecentExpenses_WhenViewModelCreated(): Unit = runBlocking {
        // Given
        `when`(expenseRepository.getRecentExpenses(anyInt(), anyInt())).thenReturn(dummyExpenses)

        // Then
        val expenses = viewModel.expenses.first()
        assertEquals(expenses.size, dummyExpenses.size)
        assertTrue(expenses.any { it.id == dummyExpenses.first().id })
    }
}
package com.vraj.spendwise.ui.totalexpenses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.ui.base.BaseButton
import com.vraj.spendwise.ui.base.BaseModalBottomSheet
import com.vraj.spendwise.ui.base.TopBar
import com.vraj.spendwise.viewmodel.MainViewModel

@Composable
fun TotalExpensesScreen(navHostController: NavHostController, viewModel: MainViewModel) {
    val showMonthFilterBottomSheet by viewModel.showMonthFilterBottomSheet.collectAsState()
    val selectedMonthAndYear by viewModel.selectedMonthAndYear.collectAsState()
    val monthAndYearList by viewModel.monthAndYears.collectAsState()
    val filteredExpenses by viewModel.filteredExpenses.collectAsState()
    val overallTotal by viewModel.overallTotal.collectAsState(initial = 0)

    LaunchedEffect(true) {
        viewModel.setCurrentMonthAndYearAsSelected()
    }

    if (showMonthFilterBottomSheet) {
        BaseModalBottomSheet(
            onDismiss = { viewModel.showMonthFilterBottomSheet(false) },
            content = {
                MonthAndYearList(
                    monthAndYearList = monthAndYearList,
                    onItemClick = {
                        with(viewModel) {
                            setSelectedMonthAndYear(it)
                            showMonthFilterBottomSheet(false)
                        }
                    }
                )
            }
        )
    }

    Scaffold(
        topBar = {
            TopBar(
                onBackButtonClicked = { navHostController.navigateUp() },
                centerText = stringResource(R.string.txt_expenses_title)
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(paddingValues)
                .padding(top = 40.dp)
                .padding(horizontal = 16.dp)
        ) {
            BaseButton(text = selectedMonthAndYear) {
                viewModel.showMonthFilterBottomSheet(true)
            }

            TotalExpensesBlock(
                modifier = Modifier.padding(bottom = 20.dp),
                filteredExpenses = filteredExpenses,
                overallTotal = overallTotal.toString()
            )
        }
    }
}

@Composable
private fun MonthAndYearList(
    modifier: Modifier = Modifier,
    monthAndYearList: List<String>,
    onItemClick: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp)
            .nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        items(monthAndYearList) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onItemClick(it) }
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_forward_half_arrow),
                    contentDescription = "Select month to filter arrow"
                )
            }
        }
    }
}

@Composable
private fun TotalExpensesBlock(
    modifier: Modifier = Modifier,
    filteredExpenses: List<ExpenseEntity>,
    overallTotal: String
) {
    if (filteredExpenses.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight()
                .padding(bottom = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_no_expense_addded),
                contentDescription = "",
                modifier = modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
                    .height(90.dp)
            )
        }
        return
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
        ) {
            ExpenseTotalListHeader()
            ExpenseTotalList(expenses = filteredExpenses)
        }

        OverallTotalBlock(overallTotal = overallTotal)
    }
}

@Composable
private fun ExpenseTotalListHeader(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.txt_expense_type),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = stringResource(id = R.string.txt_total),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExpenseTotalList(modifier: Modifier = Modifier, expenses: List<ExpenseEntity>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        items(expenses) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .basicMarquee()
                )

                Text(
                    text = it.amount.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun OverallTotalBlock(modifier: Modifier = Modifier, overallTotal: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.txt_overall_total),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = overallTotal,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
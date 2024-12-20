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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.ui.base.BaseModalBottomSheet
import com.vraj.spendwise.ui.base.TopBar
import com.vraj.spendwise.ui.inputexpense.EmptyExpenseView
import com.vraj.spendwise.util.extension.toStringByLimitingDecimalDigits
import com.vraj.spendwise.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TotalExpensesScreen(navHostController: NavHostController, viewModel: MainViewModel) {
    val selectedMonthAndYear by viewModel.selectedMonthAndYear.collectAsState()
    val filteredExpenses by viewModel.filteredExpenses.collectAsState()
    val overallTotal by viewModel.overallTotal.collectAsState(initial = 0)

    LaunchedEffect(true) {
        viewModel.setCurrentMonthAndYearAsSelected()
    }

    ShowMonthFilterBottomSheet(viewModel)

    Scaffold(
        topBar = {
            TopBar(
                onBackButtonClicked = { navHostController.navigateUp() },
                centerText = stringResource(R.string.txt_expenses_title)
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(paddingValues)
                .padding(top = 40.dp)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        viewModel.showMonthFilterBottomSheet(true)
                    }
            ) {
                Text(
                    text = selectedMonthAndYear,
                    style = MaterialTheme.typography.labelMedium,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = ""
                )
            }

            TotalExpensesBlock(
                modifier = Modifier.padding(bottom = 20.dp),
                filteredExpenses = filteredExpenses,
                overallTotal = overallTotal.toString()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowMonthFilterBottomSheet(viewModel: MainViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val showMonthFilterBottomSheet by viewModel.showMonthFilterBottomSheet.collectAsState()
    val monthAndYearList by viewModel.monthAndYears.collectAsState()

    LaunchedEffect(showMonthFilterBottomSheet) {
        if (showMonthFilterBottomSheet) sheetState.show() else sheetState.hide()
    }

    if (sheetState.isVisible || showMonthFilterBottomSheet) {
        BaseModalBottomSheet(
            sheetSate = sheetState,
            onDismiss = { viewModel.showMonthFilterBottomSheet(false) },
            content = {
                MonthAndYearList(
                    monthAndYearList = monthAndYearList,
                    onItemClick = {
                        hideBottomSheetWithAnimation(sheetState, scope) {
                            viewModel.apply {
                                showMonthFilterBottomSheet(false)
                                setSelectedMonthAndYear(it)
                            }
                        }
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun hideBottomSheetWithAnimation(
    sheetState: SheetState,
    scope: CoroutineScope,
    onCompletion: () -> Unit
) {
    scope.launch {
        sheetState.hide()
    }.invokeOnCompletion {
        onCompletion()
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
                    color = MaterialTheme.colorScheme.onBackground
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
        ) { EmptyExpenseView(modifier) }
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
            .fillMaxHeight(0.85f)
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
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f)
                        .basicMarquee(iterations = 2)
                )

                Text(
                    text = it.amount.toStringByLimitingDecimalDigits(3),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
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
            .wrapContentHeight()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.txt_overall_total),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(end = 16.dp)
                .weight(1f)
        )

        Text(
            text = overallTotal,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}
package com.vraj.spendwise.ui.inputexpense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vraj.spendwise.R
import com.vraj.spendwise.data.expense.ExpenseCategoryFinder.getCategory
import com.vraj.spendwise.ui.EmptyExpenseView
import com.vraj.spendwise.ui.HandleAlertDialog
import com.vraj.spendwise.ui.HandleToast
import com.vraj.spendwise.ui.base.BaseButton
import com.vraj.spendwise.ui.base.BaseTextField
import com.vraj.spendwise.ui.base.BaseTextFieldWithDropdown
import com.vraj.spendwise.util.MainScreen
import com.vraj.spendwise.viewmodel.InputExpenseViewModel
import com.vraj.spendwise.viewmodel.InputExpenseViewModel.Companion.NUMBER_OF_ROWS_OF_RECENT_EXPENSES
import com.vraj.spendwise.viewmodel.InputExpenseViewModel.Companion.RECENT_EXPENSE_SINGLE_ITEM_HEIGHT
import com.vraj.spendwise.viewmodel.InputExpenseViewModel.Companion.SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES

@Composable
fun InputExpenseScreen(navHostController: NavHostController) {
    val viewModel: InputExpenseViewModel = hiltViewModel()
    val scrollState = rememberScrollState()

    HandleToast(viewModel)
    HandleAlertDialog(viewModel)
    RecentExpenseBottomSheet(viewModel)
    CategorySelectionBottomSheet(viewModel)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_icon_medium),
            contentDescription = "App icon at the top",
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier.size(150.dp)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        ExpenseInputBlock(
            viewModel = viewModel,
            modifier = Modifier.padding(top = 60.dp)
        )

        AddOrViewExpenseButtonsBlock(
            navHostController = navHostController,
            viewModel = viewModel,
            modifier = Modifier.padding(top = 30.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier
                .padding(top = 48.dp, bottom = 30.dp)
                .fillMaxWidth()
        ) {
            RecentExpensesTitleBlock(viewModel)
            RecentExpensesGridBlock(viewModel)
        }
    }
}

@Composable
private fun ExpenseInputBlock(viewModel: InputExpenseViewModel, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val expenseType by viewModel.expenseType.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val expenseTypeDropdownItems by viewModel.expenseTypeDropdownItems.collectAsState()
    val isDropdownExpanded by viewModel.isDropdownExpanded.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()

    LaunchedEffect(expenseType) {
        viewModel.setCurrentCategory(getCategory(expenseType.text))
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.fillMaxWidth()
        ) {
            BaseTextFieldWithDropdown(
                textFieldValue = expenseType,
                onValueChanged = viewModel::setExpenseType,
                placeholder = stringResource(R.string.txt_expense_type),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                onDismissRequest = { viewModel.setDropdownExpanded(false) },
                isDropdownExpanded = isDropdownExpanded,
                list = expenseTypeDropdownItems
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BaseTextField(
                textFieldValue = amount,
                onValueChanged = viewModel::setAmount,
                placeholder = stringResource(R.string.txt_amount),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.weight(0.5f)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(0.5f)
                    .height(52.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Color.Transparent)
                    .border(
                        1.5.dp,
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.shapes.small
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { viewModel.setShowCategorySelectionBottomSheet(true) }
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Image(
                        painter = painterResource(currentCategory.getIcon()),
                        contentDescription = "Category type icon",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = currentCategory.getName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = "Category selection dropdown icon",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
    }
}

@Composable
private fun AddOrViewExpenseButtonsBlock(
    navHostController: NavHostController,
    viewModel: InputExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val isEntityEditInProgress by viewModel.isEntityEditInProgress.collectAsState()

    val addEditButtonTitle by remember {
        derivedStateOf {
            if (isEntityEditInProgress) R.string.txt_confirm else R.string.txt_add_expense
        }
    }
    val showOrCancelEditButtonTitle by remember {
        derivedStateOf {
            if (isEntityEditInProgress) R.string.txt_cancel else R.string.txt_show_expense
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        BaseButton(text = stringResource(id = addEditButtonTitle)) {
            viewModel.validateInputAndAddToDatabase()
            focusManager.clearFocus()
        }

        BaseButton(
            text = stringResource(id = showOrCancelEditButtonTitle),
            backgroundColor = MaterialTheme.colorScheme.background,
            textColor = MaterialTheme.colorScheme.onBackground
        ) {
            if (isEntityEditInProgress) {
                viewModel.clearEditMode()
                focusManager.clearFocus()
                return@BaseButton
            }
            navHostController.navigate(MainScreen.TotalExpensesScreen.route)
        }
    }
}

@Composable
private fun RecentExpensesTitleBlock(
    viewModel: InputExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val hasMoreExpenseToLoad by viewModel.hasMoreExpenseToLoad.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.txt_recents),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = stringResource(id = R.string.txt_load_more),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (hasMoreExpenseToLoad) viewModel.loadRecentExpenses()
                }
                .alpha(if (hasMoreExpenseToLoad) 1f else 0.5f)
        )
    }
}

@Composable
private fun RecentExpensesGridBlock(
    viewModel: InputExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.expenses.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }

    if (expenses.isEmpty()) {
        EmptyExpenseView(modifier)
        return
    }

    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(NUMBER_OF_ROWS_OF_RECENT_EXPENSES),
        horizontalItemSpacing = 20.dp,
        verticalArrangement = Arrangement.spacedBy(SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(
                ((NUMBER_OF_ROWS_OF_RECENT_EXPENSES * RECENT_EXPENSE_SINGLE_ITEM_HEIGHT) +
                        (SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES *
                                (NUMBER_OF_ROWS_OF_RECENT_EXPENSES - 1))).dp
            )
    ) {
        items(expenses) {
            Row(
                horizontalArrangement = Arrangement.spacedBy((-12).dp),
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        viewModel.apply {
                            setExpenseBottomSheetEntity(it)
                            setExpenseBottomSheetState(true)
                        }
                    }
            ) {
                Text(
                    text = it.name,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Left,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .widthIn(max = 100.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(12.dp)
                        .zIndex(1f)
                )

                Text(
                    text = it.amountString,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 12.dp)
                        .padding(start = 24.dp, end = 12.dp)
                )
            }
        }
    }
}

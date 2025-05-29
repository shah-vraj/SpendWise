package com.vraj.spendwise.ui.inputexpense

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.NavHostController
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.ui.base.BaseButton
import com.vraj.spendwise.ui.base.BaseConfirmationDialog
import com.vraj.spendwise.ui.base.BaseModalBottomSheet
import com.vraj.spendwise.ui.base.BaseTextField
import com.vraj.spendwise.ui.base.BaseTextFieldWithDropdown
import com.vraj.spendwise.ui.model.AlertDialogData
import com.vraj.spendwise.util.AppToast
import com.vraj.spendwise.util.MainScreen
import com.vraj.spendwise.viewmodel.MainViewModel
import com.vraj.spendwise.viewmodel.MainViewModel.Companion.NUMBER_OF_ROWS_OF_RECENT_EXPENSES
import com.vraj.spendwise.viewmodel.MainViewModel.Companion.RECENT_EXPENSE_SINGLE_ITEM_HEIGHT
import com.vraj.spendwise.viewmodel.MainViewModel.Companion.SPACING_BETWEEN_ROWS_OF_RECENT_EXPENSES
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun InputExpenseScreen(navHostController: NavHostController, viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    HandleToast(viewModel)
    HandleAlertDialog(viewModel)
    ShowRecentExpensesBottomSheetWhenNeeded(viewModel)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_icon_medium),
            contentDescription = "App icon at the top",
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center,
            modifier = Modifier
                .height(150.dp)
                .width(150.dp)
                .padding(top = 20.dp)
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

        RecentExpensesListBlock(
            viewModel = viewModel,
            modifier = Modifier.padding(top = 48.dp, bottom = 30.dp)
        )
    }
}

@Composable
private fun ExpenseInputBlock(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val expenseType by viewModel.expenseType.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val expenseTypeDropdownItems by viewModel.expenseTypeDropdownItems.collectAsState()
    val isDropdownExpanded by viewModel.isDropdownExpanded.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
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
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            onDismissRequest = { viewModel.setDropdownExpanded(false) },
            isDropdownExpanded = isDropdownExpanded,
            list = expenseTypeDropdownItems
        )

        BaseTextField(
            textFieldValue = amount,
            onValueChanged = viewModel::setAmount,
            placeholder = stringResource(R.string.txt_amount),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.clearFocus() }
            )
        )
    }
}

@Composable
private fun AddOrViewExpenseButtonsBlock(
    navHostController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        BaseButton(text = stringResource(id = R.string.txt_add_expense)) {
            viewModel.validateInputAndAddToDatabase()
            focusManager.clearFocus()
        }

        BaseButton(
            text = stringResource(id = R.string.txt_show_expense),
            backgroundColor = MaterialTheme.colorScheme.background,
            textColor = MaterialTheme.colorScheme.onBackground
        ) {
            navHostController.navigate(MainScreen.TotalExpensesScreen.route)
        }
    }
}

@Composable
private fun RecentExpensesListBlock(viewModel: MainViewModel, modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        RecentExpensesTitleBlock(viewModel)
        RecentExpensesGridBlock(viewModel)
    }
}

@Composable
private fun RecentExpensesTitleBlock(
    viewModel: MainViewModel,
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
    viewModel: MainViewModel,
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

@Composable
private fun HandleToast(viewModel: MainViewModel) {
    val context = LocalContext.current
    val showToast by viewModel.showToast.collectAsState()

    when (val toast = showToast) {
        is AppToast.Error -> Toasty.error(context, toast.message).show()
        is AppToast.Success -> Toasty.success(context, toast.message).show()
        AppToast.Nothing -> {}
    }.also { viewModel.onToastShown() }
}

@Composable
fun HandleAlertDialog(viewModel: MainViewModel) {
    val alertDialogData by viewModel.showAlertDialog.collectAsState()

    alertDialogData?.let {
        BaseConfirmationDialog(
            title = it.title,
            message = it.message,
            subMessage = it.subMessage,
            onConfirm = { it.onConfirmAction() },
            onCancel = { viewModel.showAlertDialog(null) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowRecentExpensesBottomSheetWhenNeeded(viewModel: MainViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val expenseBottomSheetState by viewModel.expenseBottomSheetState.collectAsState()
    val expenseEntity by viewModel.expenseBottomSheetEntity.collectAsState()

    LaunchedEffect(expenseBottomSheetState) {
        if (expenseBottomSheetState) sheetState.show() else sheetState.hide()
    }

    if (sheetState.isVisible || expenseBottomSheetState) {
        BaseModalBottomSheet(
            sheetSate = sheetState,
            onDismiss = {
                viewModel.apply {
                    setExpenseBottomSheetState(false)
                    setExpenseBottomSheetEntity(null)
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(40.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 50.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = expenseEntity?.name.orEmpty(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                        text = expenseEntity?.amountString.orEmpty(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = expenseEntity?.createdDateFormatted.orEmpty(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BaseButton(text = stringResource(id = R.string.txt_add_again)) {
                        hideBottomSheetWithAnimation(sheetState, scope) {
                            expenseEntity?.let {
                                onHideBottomSheetForAdd(viewModel, it) { id ->
                                    context.getString(id)
                                }
                            }
                        }
                    }

                    BaseButton(
                        text = stringResource(id = R.string.txt_remove),
                        backgroundColor = MaterialTheme.colorScheme.background,
                        textColor = MaterialTheme.colorScheme.onBackground
                    ) {
                        hideBottomSheetWithAnimation(sheetState, scope) {
                            expenseEntity?.let {
                                onHideBottomSheetForRemove(viewModel, it) { id ->
                                    context.getString(id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyExpenseView(modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_no_expense_added),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .height(60.dp)
        )

        Text(
            text = stringResource(id = R.string.txt_no_expense_added),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
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

private fun onHideBottomSheetForAdd(
    viewModel: MainViewModel,
    expenseEntity: ExpenseEntity,
    getStringResource: (Int) -> String
) {
    viewModel.apply {
        setExpenseBottomSheetState(false)
        setExpenseBottomSheetEntity(null)
        val alertDialogData = AlertDialogData(
            title = getStringResource(R.string.txt_add_expense_again_title),
            message = getStringResource(R.string.txt_add_expense_again_message),
            subMessage = "${expenseEntity.name} - ${expenseEntity.amountString}",
            onConfirmAction = {
                addExpense(expenseEntity)
                showToast(AppToast.Success(R.string.add_expense_success))
                showAlertDialog(null)
            }
        )
        showAlertDialog(alertDialogData)
    }
}

private fun onHideBottomSheetForRemove(
    viewModel: MainViewModel,
    expenseEntity: ExpenseEntity,
    getStringResource: (Int) -> String
) {
    viewModel.apply {
        setExpenseBottomSheetState(false)
        setExpenseBottomSheetEntity(null)
        val alertDialogData = AlertDialogData(
            title = getStringResource(R.string.txt_remove_expense_title),
            message = getStringResource(R.string.txt_remove_expense_message),
            subMessage = "${expenseEntity.name} - ${expenseEntity.amountString}",
            onConfirmAction = {
                removeExpense(expenseEntity.id)
                showToast(AppToast.Success(R.string.remove_expense_success))
                showAlertDialog(null)
            }
        )
        showAlertDialog(alertDialogData)
    }
}
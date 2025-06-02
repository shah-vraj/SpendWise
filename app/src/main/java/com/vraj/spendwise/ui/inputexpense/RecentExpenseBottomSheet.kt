package com.vraj.spendwise.ui.inputexpense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.ui.base.BaseButton
import com.vraj.spendwise.ui.base.BaseModalBottomSheet
import com.vraj.spendwise.ui.model.AlertDialogData
import com.vraj.spendwise.util.AppToast
import com.vraj.spendwise.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentExpenseBottomSheet(viewModel: MainViewModel) {
    val sheetState = rememberModalBottomSheetState()
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
                expenseEntity?.let {
                    Header(
                        expenseEntity = it,
                        modifier = Modifier.fillMaxWidth()
                    )

                    ActionButtons(
                        sheetState = sheetState,
                        expenseEntity = it,
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(expenseEntity: ExpenseEntity, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = expenseEntity.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = expenseEntity.amountString,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = expenseEntity.createdDateFormatted,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButtons(
    expenseEntity: ExpenseEntity,
    sheetState: SheetState,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BaseButton(
                text = stringResource(id = R.string.txt_edit),
                modifier = Modifier.weight(0.45f)
            ) {
                hideBottomSheetWithAnimation(sheetState, scope) {
                    viewModel.apply {
                        setExpenseBottomSheetState(false)
                        setExpenseBottomSheetEntity(null)
                        edit(expenseEntity)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            BaseButton(
                text = stringResource(id = R.string.txt_add_again),
                modifier = Modifier.weight(0.45f)
            ) {
                hideBottomSheetWithAnimation(sheetState, scope) {
                    onHideBottomSheetForAdd(viewModel, expenseEntity) { id ->
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
                onHideBottomSheetForRemove(viewModel, expenseEntity) { id ->
                    context.getString(id)
                }
            }
        }
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
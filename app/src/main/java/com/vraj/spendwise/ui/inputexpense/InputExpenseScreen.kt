package com.vraj.spendwise.ui.inputexpense

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.vraj.spendwise.R
import com.vraj.spendwise.data.local.entity.ExpenseEntity
import com.vraj.spendwise.ui.base.BaseButton
import com.vraj.spendwise.ui.base.BaseTextField
import com.vraj.spendwise.ui.theme.BlueText
import com.vraj.spendwise.ui.theme.LightGray
import com.vraj.spendwise.util.MainScreen
import com.vraj.spendwise.viewmodel.MainViewModel
import es.dmoral.toasty.Toasty

@Composable
fun InputExpenseScreen(navHostController: NavHostController, viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

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
                .height(200.dp)
                .width(150.dp)
                .padding(top = 20.dp)
        )

        ExpenseInputBlock(
            viewModel = viewModel,
            modifier = Modifier.padding(top = 80.dp)
        )

        AddOrViewExpenseButtonsBlock(
            navHostController = navHostController,
            viewModel = viewModel,
            modifier = Modifier.padding(top = 30.dp)
        )
    }
}

@Composable
private fun ExpenseInputBlock(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val expenseType by viewModel.expenseType.collectAsState()
    val amount by viewModel.amount.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        BaseTextField(
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
            )
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
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
        BaseButton(text = stringResource(id = R.string.txt_add_expense)) {
            if (validateInputAndAddToDatabase(viewModel))
                Toasty.success(context, R.string.add_expense_success).show()
            else
                Toasty.error(context, R.string.invalid_input_error).show()
        }

        BaseButton(
            text = stringResource(id = R.string.txt_show_expense),
            backgroundColor = LightGray,
            textColor = BlueText
        ) {
            navHostController.navigate(MainScreen.GraphicalDataScreen.route)
        }
    }
}

private fun validateInputAndAddToDatabase(viewModel: MainViewModel): Boolean {
    val expenseType = viewModel.expenseType.value
    val amount = viewModel.amountInDouble.value ?: return false

    if (expenseType.isBlank() || amount <= 0f)
        return false

    viewModel.checkIfExpenseExists(expenseType) {
        it?.let {
            viewModel.updateExpense(it.name, it.amount + amount)
        } ?: viewModel.addExpense(ExpenseEntity(name = expenseType, amount = amount))
    }
    return true
}

package com.vraj.spendwise.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vraj.spendwise.R
import com.vraj.spendwise.ui.base.BaseConfirmationDialog
import com.vraj.spendwise.ui.base.BaseViewModel
import com.vraj.spendwise.util.AppToast
import es.dmoral.toasty.Toasty

@Composable
fun HandleToast(viewModel: BaseViewModel) {
    val context = LocalContext.current
    val showToast by viewModel.showToast.collectAsState()

    when (val toast = showToast) {
        is AppToast.Error -> Toasty.error(context, toast.message).show()
        is AppToast.Success -> Toasty.success(context, toast.message).show()
        is AppToast.Info -> Toasty.info(context, toast.message, Toast.LENGTH_LONG).show()
        AppToast.Nothing -> {}
    }.also { viewModel.onToastShown() }
}

@Composable
fun HandleAlertDialog(viewModel: BaseViewModel) {
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
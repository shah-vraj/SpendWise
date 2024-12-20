package com.vraj.spendwise.ui.base

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vraj.spendwise.R

@Composable
fun BaseConfirmationDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    subMessage: String,
    confirmButtonText: String = stringResource(R.string.txt_confirm),
    cancelButtonText: String = stringResource(R.string.txt_cancel),
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = subMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            BaseButton(
                text = confirmButtonText,
                onButtonClicked = { onConfirm() }
            )
        },
        dismissButton = {
            BaseButton(
                text = cancelButtonText,
                backgroundColor = MaterialTheme.colorScheme.background,
                textColor = MaterialTheme.colorScheme.onBackground,
                onButtonClicked = { onCancel() }
            )
        },
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

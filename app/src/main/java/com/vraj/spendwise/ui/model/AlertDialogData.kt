package com.vraj.spendwise.ui.model

data class AlertDialogData(
    val title: String,
    val message: String,
    val subMessage: String,
    val onConfirmAction: () -> Unit
)

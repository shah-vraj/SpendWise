package com.vraj.spendwise.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vraj.spendwise.ui.model.AlertDialogData
import com.vraj.spendwise.util.AppToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel: ViewModel() {

    protected val _showToast = MutableStateFlow<AppToast>(AppToast.Nothing)
    val showToast = _showToast.asStateFlow()

    private val _showAlertDialog = MutableStateFlow<AlertDialogData?>(null)
    val showAlertDialog = _showAlertDialog.asStateFlow()

    private var debounceJob: Job? = null

    fun showToast(appToast: AppToast) {
        _showToast.value = appToast
    }

    fun onToastShown() {
        _showToast.value = AppToast.Nothing
    }

    fun showAlertDialog(alertDialogData: AlertDialogData?) {
        _showAlertDialog.value = alertDialogData
    }

    protected fun debounce(
        delay: Long,
        coroutineContext: CoroutineContext = Dispatchers.Main,
        action: () -> Unit
    ) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch(coroutineContext) {
            delay(delay)
            action.invoke()
        }
    }
}
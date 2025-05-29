package com.vraj.spendwise.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel: ViewModel() {

    private var debounceJob: Job? = null

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
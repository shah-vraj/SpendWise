package com.vraj.spendwise.util

import androidx.annotation.StringRes

sealed class AppToast {
    data class Success(@StringRes val message: Int) : AppToast()
    data class Error(@StringRes val message: Int) : AppToast()
    data object Nothing : AppToast()
}
package com.vraj.spendwise.util.extension

import kotlin.math.pow

fun Double.toStringByLimitingDecimalDigits(numberOfDigits: Int): String =
    ((this * 10.0.pow(numberOfDigits)).toInt() / 10.0.pow(numberOfDigits)).toString()
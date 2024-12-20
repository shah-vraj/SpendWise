package com.vraj.spendwise.util.extension

import java.math.BigDecimal
import java.text.NumberFormat
import kotlin.math.pow

fun Double.toStringByLimitingDecimalDigits(numberOfDigits: Int): String {
    val tenPowerNumberOfDigits = BigDecimal(10.0.pow(numberOfDigits))
    val bigDecimal = ((BigDecimal(this) * tenPowerNumberOfDigits) / tenPowerNumberOfDigits)
    return NumberFormat.getInstance().let {
        it.maximumFractionDigits = numberOfDigits
        it.isGroupingUsed = false
        it.format(bigDecimal)
    }
}
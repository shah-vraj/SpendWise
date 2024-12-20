package com.vraj.spendwise.util.extension

fun String.isLastCharValid(): Boolean {
    return if (isEmpty()) {
        true
    } else {
        val last = last()
        last.isLetter() || last.isDigit() || last.isWhitespace()
    }
}
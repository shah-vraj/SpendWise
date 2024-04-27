package com.vraj.spendwise.util

enum class MonthOfYear(private val numericString: String, private val monthString: String) {
    JANUARY("01", "January"),
    FEBRUARY("02", "February"),
    MARCH("03", "March"),
    APRIL("04", "April"),
    MAY("05", "May"),
    JUNE("06", "June"),
    JULY("07", "July"),
    AUGUST("08", "August"),
    SEPTEMBER("09", "September"),
    OCTOBER("10", "October"),
    NOVEMBER("11", "November"),
    DECEMBER("12", "December");

    companion object {
        fun getMonthStringFromNumericString(numberString: String): String =
            entries.find { it.numericString == numberString }?.monthString ?: ""

        fun getNumericStringFromMonthString(monthString: String): String =
            entries.find { it.monthString == monthString }?.numericString ?: ""
    }
}
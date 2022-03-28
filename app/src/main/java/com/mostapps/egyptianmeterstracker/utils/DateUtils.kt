package com.mostapps.egyptianmeterstracker.utils

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object DateUtils {

    const val DEFAULT_FORMAT_DATE = "dd/MM/yyyy"
    val DATE_PATTERN =
        Pattern.compile("^(?:(?!0000)[0-9]{4}([-/.]?)(?:(?:0?[1-9]|1[0-2])([-/.]?)(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])([-/.]?)(?:29|30)|(?:0?[13578]|1[02])([-/.]?)31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-/.]?)0?2([-/.]?)29)$")

    fun validateDateFormat(dateText: String?): Boolean {
        return DATE_PATTERN.matcher(dateText).matches()
    }

    fun now(): Timestamp {
        return Timestamp(System.currentTimeMillis())
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(date: Date?, formatStr: String?): String {
        return SimpleDateFormat(formatStr ?: DEFAULT_FORMAT_DATE).format(
            date!!
        )
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun formatDate(dateStr: String?, formatStr: String?): Date {
        return SimpleDateFormat(formatStr ?: DEFAULT_FORMAT_DATE).parse(
            dateStr!!
        )!!
    }

    fun getYear(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.YEAR]
    }

    @Throws(ParseException::class)
    fun getYear(dateStr: String?, format: String?): Int {
        return getYear(
            formatDate(
                dateStr,
                format
            )
        )
    }

    private fun getMonth(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.MONTH] + 1
    }

    @Throws(ParseException::class)
    fun getMonth(dateStr: String?, format: String?): Int {
        return getMonth(
            formatDate(
                dateStr,
                format
            )
        )
    }

    private fun getDay(date: Date?): Int {
        val c = Calendar.getInstance()
        c.time = date
        return c[Calendar.DAY_OF_MONTH]
    }

    @Throws(ParseException::class)
    fun getDay(dateStr: String?, format: String?): Int {
        return getDay(
            formatDate(
                dateStr,
                format
            )
        )
    }


}
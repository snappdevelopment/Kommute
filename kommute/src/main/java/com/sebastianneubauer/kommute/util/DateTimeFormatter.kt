package com.sebastianneubauer.kommute.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.FormatStyle
import java.time.format.DateTimeFormatter as JavaDateTimeFormatter

internal interface DateTimeFormatter {
    fun format(timestamp: Long): String
}

internal class LocalDateTimeFormatter : DateTimeFormatter {
    override fun format(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val timeFormatter = JavaDateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
        return localDateTime.toLocalTime().format(timeFormatter)
    }
}

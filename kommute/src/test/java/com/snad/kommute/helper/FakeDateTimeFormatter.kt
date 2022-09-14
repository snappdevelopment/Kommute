package com.snad.kommute.helper

import com.snad.kommute.util.DateTimeFormatter

internal class FakeDateTimeFormatter: DateTimeFormatter {
    override fun format(timestamp: Long): String {
        return ""
    }
}
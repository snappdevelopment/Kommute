package com.sebastianneubauer.kommute.helper

import com.sebastianneubauer.kommute.util.DateTimeFormatter

internal class FakeDateTimeFormatter: DateTimeFormatter {
    override fun format(timestamp: Long): String {
        return ""
    }
}
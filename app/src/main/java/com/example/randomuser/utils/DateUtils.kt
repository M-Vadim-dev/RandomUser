package com.example.randomuser.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    private val displayFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
            .withZone(ZoneId.systemDefault())

    fun formatDob(isoDate: String?): String {
        if (isoDate.isNullOrBlank()) return "-"
        return try {
            val instant = Instant.parse(isoDate)
            displayFormatter.format(instant)
        } catch (_: Exception) {
            isoDate
        }
    }
}

package com.example.billtracker.domain.model

import java.util.Calendar

/** @param date epoch millis ของวันหยุด (เที่ยงคืนของวันนั้น) */
data class Holiday(
    val date: Long,
    val name: String
)


fun List<Holiday>.matchingWithinDays(dueDateMillis: Long, leadDays: Int = 3): List<Holiday> {
    val dueDateStart = startOfDay(dueDateMillis)
    val earliestRelevant = dueDateStart - (leadDays * 24 * 60 * 60 * 1000L)

    return filter { holiday ->
        val holidayStart = startOfDay(holiday.date)
        holidayStart in earliestRelevant..dueDateStart
    }.sortedBy { it.date }
}

private fun startOfDay(millis: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

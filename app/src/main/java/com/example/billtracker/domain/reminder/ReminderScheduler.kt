package com.example.billtracker.domain.reminder

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.domain.model.effectiveReminderStart
import java.util.Calendar

object ReminderScheduler {


    fun currentSlot(now: Calendar): String? {
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        if (minute >= 15) return null
        return when (hour) {
            9 -> "09:00"
            13 -> "13:00"
            20 -> "20:00"
            else -> null
        }
    }

    fun shouldNotify(bill: Bill, slot: String, now: Calendar): Boolean {
        return when (bill.reminderType) {
            ReminderType.NONE -> false


                ReminderType.DAILY ->
                (slot == "20:00" || slot == "09:00" || slot == "13:00") && !isBeforeStartDate(bill, now)

            ReminderType.MONTHLY -> {
                if (isBeforeStartDate(bill, now)) return false
                val daysSinceStart = daysBetween(bill.effectiveReminderStart(), now.timeInMillis)
                val isOverdue = isSameOrAfterDay(now.timeInMillis, bill.dueDate)

                when {
                    daysSinceStart in 0..1 -> true // 2 วันแรก - เตือนได้ทุก slot
                    isOverdue -> slot == "09:00" // เกินกำหนดแล้ว - เตือนวันละ 1 ครั้งต่อเนื่อง
                    else -> false // อยู่ระหว่างกลาง - เงียบ
                }
            }
        }
    }

    private fun isBeforeStartDate(bill: Bill, now: Calendar): Boolean =
        startOfDay(now.timeInMillis) < startOfDay(bill.effectiveReminderStart())

    private fun daysBetween(startMillis: Long, endMillis: Long): Long {
        val start = startOfDay(startMillis)
        val end = startOfDay(endMillis)
        return (end - start) / (24 * 60 * 60 * 1000L)
    }

    private fun isSameOrAfterDay(millisA: Long, millisB: Long): Boolean =
        startOfDay(millisA) >= startOfDay(millisB)

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
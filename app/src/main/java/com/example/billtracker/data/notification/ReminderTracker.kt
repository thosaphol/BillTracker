package com.example.billtracker.data.notification

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderTracker(context: Context) {

    private val prefs = context.getSharedPreferences("reminder_tracker", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun keyFor(billId: Int, slot: String): String {
        val today = dateFormat.format(Date())
        return "notified_${billId}_${today}_$slot"
    }

    fun hasNotified(billId: Int, slot: String): Boolean =
        prefs.getBoolean(keyFor(billId, slot), false)

    fun markNotified(billId: Int, slot: String) {
        prefs.edit().putBoolean(keyFor(billId, slot), true).apply()
    }
}
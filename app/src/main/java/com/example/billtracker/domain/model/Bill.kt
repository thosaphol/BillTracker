package com.example.billtracker.domain.model

import com.example.billtracker.domain.BillStatus
import com.example.billtracker.domain.ReminderType

data class Bill(
    val id: Long = 0,

    val title: String,

    val amount: Double,


    val dueDate: Long,


    val categoryId: Int,


    val isPaid: Boolean = false,

    val note: String = "",


    val reminderType: ReminderType = ReminderType.NONE,


    val reminderStartDate: Long? = null,


    val createdAt: Long = System.currentTimeMillis()
)
fun Bill.status(now: Long = System.currentTimeMillis()): BillStatus = when {
    isPaid -> BillStatus.PAID
    dueDate < now -> BillStatus.OVERDUE
    else -> BillStatus.UNPAID
}

package com.example.billtracker.domain.model


data class Bill(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val dueDate: Long,                 // epoch millis
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


fun Bill.effectiveReminderStart(): Long = reminderStartDate ?: dueDate

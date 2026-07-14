package com.example.billtracker.domain.model


/**
 * Domain model ของรายการค้างจ่าย 1 รายการ
 *
 * @param reminderStartDate nullable — ถ้า null ให้ใช้ dueDate เป็นค่าเริ่มต้นตอนคำนวณวันเริ่มเตือน
 *        (ตาม requirement: "custom" ไม่ใช่ตัวเลือกที่ 4 แต่คือการเปลี่ยนวันเริ่มต้นของ reminderType เดิม)
 */
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

/**
 * คำนวณสถานะทุกครั้งที่ต้องใช้ ไม่เก็บเป็น field ตรงๆ ใน Bill
 * เพื่อกัน state ไม่ตรงกัน (เช่น isPaid=true แต่ status ยังค้างเป็น OVERDUE)
 */
fun Bill.status(now: Long = System.currentTimeMillis()): BillStatus = when {
    isPaid -> BillStatus.PAID
    dueDate < now -> BillStatus.OVERDUE
    else -> BillStatus.UNPAID
}

/**
 * วันที่ใช้เริ่มคำนวณรอบแจ้งเตือนจริง (fallback ไป dueDate ถ้าไม่ได้ตั้ง reminderStartDate เอง)
 * ใช้ตัวนี้ในฝั่ง WorkManager/UseCase แทนการเช็ค null ซ้ำๆ ทุกที่
 */
fun Bill.effectiveReminderStart(): Long = reminderStartDate ?: dueDate

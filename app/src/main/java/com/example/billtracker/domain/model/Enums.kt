package com.example.billtracker.domain.model

/**
 * ประเภทการแจ้งเตือน — ตาม requirement ที่ล็อกไว้ มีแค่ 3 ค่า
 * (ไม่มี WEEKLY, ไม่มี CUSTOM แยก - custom ทำผ่าน reminderStartDate ใน Bill แทน)
 */
enum class ReminderType {
    NONE,
    DAILY,
    MONTHLY
}

/**
 * สถานะของบิล — ไม่เก็บใน DB ตรงๆ คำนวณจาก Bill.isPaid + Bill.dueDate เสมอ
 * ดูฟังก์ชัน Bill.status() ในไฟล์ Bill.kt
 */
enum class BillStatus {
    PAID,
    UNPAID,
    OVERDUE
}
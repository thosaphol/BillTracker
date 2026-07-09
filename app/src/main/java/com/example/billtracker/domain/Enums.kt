package com.example.billtracker.domain

enum class ReminderType {
    NONE,
    DAILY,
    MONTHLY
}

enum class BillStatus {
    PAID,
    UNPAID,
    OVERDUE
}
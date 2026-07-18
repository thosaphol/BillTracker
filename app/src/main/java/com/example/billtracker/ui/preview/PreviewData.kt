package com.example.billtracker.ui.preview

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.domain.model.defaultCategories
import java.util.concurrent.TimeUnit


private fun daysFromNow(days: Int): Long =
    System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())

val previewCategories: List<Category> = defaultCategories + listOf(
    Category(id = 6, name = "ฟิตเนส", iconKey = "fitness_center", isCustom = true),
    Category(id = 7, name = "ค่าบัตรเครดิต", iconKey = "credit_card", isCustom = true),
)

val previewBills: List<Bill> = listOf(
    Bill(
        id = 1, title = "ค่าน้ำ", amount = 350.0,
        dueDate = daysFromNow(-5), categoryId = 3, isPaid = false,
        reminderType = ReminderType.MONTHLY
    ),
    Bill(
        id = 2, title = "ค่าไฟฟ้า", amount = 1500.0,
        dueDate = daysFromNow(3), categoryId = 2, isPaid = false,
        reminderType = ReminderType.MONTHLY
    ),
    Bill(
        id = 3, title = "ค่าอินเทอร์เน็ต", amount = 800.0,
        dueDate = daysFromNow(6), categoryId = 4, isPaid = false,
        reminderType = ReminderType.NONE
    ),
    Bill(
        id = 4, title = "ค่าโทรศัพท์", amount = 1200.0,
        dueDate = daysFromNow(-10), categoryId = 5, isPaid = true,
        reminderType = ReminderType.NONE
    ),
)
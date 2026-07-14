package com.example.billtracker.domain.model


data class BillCategory(
    val bill: Bill,
    val category: Category
)

fun BillCategory.toBill(): Bill {

    return Bill(
        id = bill.id,
        title = bill.title,
        amount = bill.amount,
        dueDate = bill.dueDate,
        categoryId = bill.categoryId,
        isPaid = bill.isPaid,
        note = bill.note,
        reminderType = bill.reminderType,
        reminderStartDate = bill.reminderStartDate,
        createdAt = bill.createdAt
    )
}

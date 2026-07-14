package com.example.billtracker.data.repository


import com.example.billtracker.data.local.BillEntity
import com.example.billtracker.data.local.CategoryEntity
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category

/**
 * ตัวแปลงระหว่าง Room Entity <-> Domain model
 * แยกไว้ที่นี่ (ไม่ใช่ extension function ใน domain) เพราะ domain ไม่ควรรู้จัก Entity เลย
 */

fun BillEntity.toDomain(): Bill = Bill(
    id = id,
    title = title,
    amount = amount,
    dueDate = dueDate,
    categoryId = categoryId,
    isPaid = isPaid,
    note = note,
    reminderType = reminderType,
    reminderStartDate = reminderStartDate,
    createdAt = createdAt
)

fun Bill.toEntity(): BillEntity = BillEntity(
    id = id,
    title = title,
    amount = amount,
    dueDate = dueDate,
    categoryId = categoryId,
    isPaid = isPaid,
    note = note,
    reminderType = reminderType,
    reminderStartDate = reminderStartDate,
    createdAt = createdAt
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconKey = iconKey,
    isCustom = isCustom
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    iconKey = iconKey,
    isCustom = isCustom
)
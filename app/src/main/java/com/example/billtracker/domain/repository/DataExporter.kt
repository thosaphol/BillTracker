package com.example.billtracker.domain.repository

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category

/**
 * แยก interface นี้ออกมาต่างหาก เพราะ "เขียนไฟล์ export" ต้องใช้ Context
 * (เช่น SAF - Storage Access Framework) ซึ่ง ViewModel ไม่ควรถือ Context โดยตรง
 * (memory leak เสี่ยงสูงถ้า ViewModel เก็บ Context ของ Activity ไว้)
 *
 * Implementation จริง (คุยกับ Context/ไฟล์) อยู่ฝั่ง data layer หรือ UI layer
 * แล้ว inject interface นี้เข้า ViewModel แทน
 */
interface DataExporter {
    suspend fun export(bills: List<Bill>, categories: List<Category>): Result<Unit>
}
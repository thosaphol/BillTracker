package com.example.billtracker.domain.repository

import androidx.lifecycle.LiveData
import com.example.billtracker.domain.model.Bill

interface BillRepository {
    fun getAllBills(): LiveData<List<Bill>>   // ไม่ต้องมี suspend เพราะ LiveData จัดการ async เอง
    suspend fun getBillById(id: Int): Bill?
    suspend fun getBillsByCategoryId(categoryId: Int): List<Bill>
    suspend fun updateBill(bill: Bill)
    suspend fun deleteBill(bill: Bill)
    suspend fun addBill(bill: Bill): Long
}
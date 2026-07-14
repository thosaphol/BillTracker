package com.example.billtracker.domain.repository

import androidx.lifecycle.LiveData
import com.example.billtracker.domain.model.Bill


interface BillRepository {

    /** LiveData เพราะ BillListScreen ต้อง auto-update ทันทีที่ Room เปลี่ยน ไม่ต้องเรียกซ้ำเอง */
    fun getAllBills(): LiveData<List<Bill>>

    suspend fun getBillById(id: Int): Bill?

    suspend fun addBill(bill: Bill): Long

    suspend fun updateBill(bill: Bill)

    suspend fun deleteBill(bill: Bill)

    /** shortcut สำหรับปุ่ม "mark as paid" ใน BillItemCard/BillDetailScreen โดยไม่ต้องส่ง Bill ทั้งก้อน */
    suspend fun markAsPaid(billId: Int, isPaid: Boolean)
    suspend fun getBillsByCategoryId(id: Int): List<Bill>
    suspend fun deleteAllBills()
}
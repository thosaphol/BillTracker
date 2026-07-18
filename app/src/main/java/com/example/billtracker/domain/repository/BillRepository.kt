package com.example.billtracker.domain.repository

import androidx.lifecycle.LiveData
import com.example.billtracker.domain.model.Bill


interface BillRepository {

    fun getAllBills(): LiveData<List<Bill>>

    suspend fun getBillById(id: Int): Bill?

    suspend fun addBill(bill: Bill): Long

    suspend fun updateBill(bill: Bill)

    suspend fun deleteBill(bill: Bill)

    suspend fun markAsPaid(billId: Int, isPaid: Boolean)
    suspend fun getBillsByCategoryId(id: Int): List<Bill>
    suspend fun deleteAllBills()
}
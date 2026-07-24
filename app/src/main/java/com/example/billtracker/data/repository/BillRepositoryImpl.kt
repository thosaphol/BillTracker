package com.example.billtracker.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.billtracker.data.local.BillDao
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.repository.BillRepository


class BillRepositoryImpl(
    private val billDao: BillDao
) : BillRepository {

    override fun getAllBills(): LiveData<List<Bill>> =
        billDao.getAllBills().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAllBillsOnce(): List<Bill> =
        billDao.getAllBillsOnce().map { it.toDomain() }

    override suspend fun getBillById(id: Int): Bill? =
        billDao.getBillById(id)?.toDomain()

    override suspend fun getBillsByCategoryId(categoryId: Int): List<Bill> =
        billDao.getBillsByCategoryId(categoryId).map { it.toDomain() }

    override suspend fun addBill(bill: Bill): Long =
        billDao.insert(bill.toEntity())

    override suspend fun updateBill(bill: Bill) =
        billDao.update(bill.toEntity())

    override suspend fun deleteBill(bill: Bill) =
        billDao.delete(bill.toEntity())

    override suspend fun markAsPaid(billId: Int, isPaid: Boolean) =
        billDao.updatePaidStatus(billId, isPaid)

    override suspend fun deleteAllBills() =
        billDao.deleteAll()
}
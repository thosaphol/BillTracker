package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.repository.BillRepository

/** shortcut สำหรับปุ่ม "ทำเครื่องหมายว่าจ่ายแล้ว" ใน BillItemCard/BillDetailScreen */
class MarkBillAsPaidUseCase (
    private val repository: BillRepository
) {
    suspend operator fun invoke(billId: Int, isPaid: Boolean): Result<Unit> = try {
        repository.markAsPaid(billId, isPaid)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
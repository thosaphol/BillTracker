package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.repository.BillRepository

class UpdateBillUseCase (
    private val repository: BillRepository
) {
    suspend operator fun invoke(bill: Bill): Result<Unit> {
        validateBill(bill)?.let { errorMessage ->
            return Result.failure(IllegalArgumentException(errorMessage))
        }
        return try {
            repository.updateBill(bill)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateBill(bill: Bill): String? {
        if (bill.title.isBlank()) return "กรุณาระบุชื่อรายการ"
        if (bill.amount <= 0) return "จำนวนเงินต้องมากกว่า 0"
        val start = bill.reminderStartDate
        if (start != null && start > bill.dueDate) {
            return "วันที่เริ่มแจ้งเตือนต้องไม่อยู่หลังวันครบกำหนด"
        }
        return null
    }
}
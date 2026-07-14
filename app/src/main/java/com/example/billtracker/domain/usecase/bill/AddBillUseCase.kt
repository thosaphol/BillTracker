package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.BillCategory
import com.example.billtracker.domain.model.toBill
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository


class AddBillUseCase (
    private val repository: BillRepository
) {
    suspend operator fun invoke(bill: Bill): Result<Long> {
        validateBill(bill)?.let { errorMessage ->
            return Result.failure(IllegalArgumentException(errorMessage))
        }
        return try {
            Result.success(repository.addBill(bill))
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

//class AddBillUseCase(
//    val billRepository: BillRepository,
//    val categoryRepository: CategoryRepository
//) {
//
//    suspend operator fun invoke(billCategory: BillCategory): Result<Long> {
//        val validateResult = validateBillCategory(billCategory)
//        if (validateResult.isFailure) {
//            return Result.failure(validateResult.exceptionOrNull()!!)
//        }
//        val newId = billRepository.addBill(billCategory.toBill())
//        return Result.success(newId)
//    }
//
//    private fun validateBillCategory(billCategory: BillCategory): Result<Unit> {
//        val bill = billCategory.bill
//
//        if (bill.title.isBlank()) {
//            return Result.failure(IllegalArgumentException("Bill title is required"))
//        }
//        if (bill.amount <= 0) {
//            return Result.failure(IllegalArgumentException("Bill amount must be greater than 0"))
//        }
//        if (bill.dueDate <= 0) {
//            return Result.failure(IllegalArgumentException("Bill dueDate is required"))
//        }
//        if (bill.reminderStartDate != null && bill.reminderStartDate <= 0) {
//            return Result.failure(IllegalArgumentException("Bill reminderStartDate is invalid"))
//        }
//        if (bill.reminderType != ReminderType.NONE && bill.reminderStartDate == null) {
//            return Result.failure(IllegalArgumentException("reminderStartDate is required when reminder is enabled"))
//        }
//
//        return Result.success(Unit)
//    }
//}
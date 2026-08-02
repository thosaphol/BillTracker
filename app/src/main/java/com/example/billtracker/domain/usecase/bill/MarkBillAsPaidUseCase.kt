package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.repository.BillRepository
import javax.inject.Inject

class MarkBillAsPaidUseCase @Inject constructor (
    private val repository: BillRepository
) {
    suspend operator fun invoke(billId: Int, isPaid: Boolean): Result<Unit> = try {
        repository.markAsPaid(billId, isPaid)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
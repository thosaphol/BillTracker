package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.repository.BillRepository
import javax.inject.Inject

class DeleteBillUseCase @Inject constructor (
    private val repository: BillRepository
) {
    suspend operator fun invoke(bill: Bill): Result<Unit> = try {
        repository.deleteBill(bill)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
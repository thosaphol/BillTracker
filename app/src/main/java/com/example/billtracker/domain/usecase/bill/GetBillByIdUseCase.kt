package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.BillCategory
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository

class GetBillByIdUseCase(
    val billRepository: BillRepository
) {

    suspend operator fun invoke(id: Int): Bill?{
        val bill = billRepository.getBillById(id) ?: return null
        return bill
    }
}
package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.BillCategory
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject

class GetBillByIdUseCase @Inject constructor(
    private val billRepository: BillRepository
) {

    suspend operator fun invoke(id: Int): Bill?{
        val bill = billRepository.getBillById(id) ?: return null
        return bill
    }
}